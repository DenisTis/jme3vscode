package com.jme3vscode.template.ai;

import com.jme3.scene.Mesh;
import org.recast4j.detour.*;
import org.recast4j.recast.*;
import org.recast4j.recast.geom.InputGeomProvider;
import org.recast4j.recast.geom.SimpleInputGeomProvider;
import org.recast4j.recast.RecastConstants.PartitionType;
import org.recast4j.recast.geom.TriMesh;

import com.jme3vscode.template.ai.RecastConfiguration;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.nio.FloatBuffer;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.io.File;
import java.io.FileWriter;
import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

/*NavMeshBuilder class: according to mitm (developer from jmonkeyengine), this link should be used:
https://hub.jmonkeyengine.org/t/navmesh-cell-size/39052/12

Progress: Navmesh is now generated!! However my mesh is getting a little distorted
 */
public class NavigationMeshBuilder {
    private final static int borderSize = 0;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public void getNavigationMesh(Mesh sourceMesh) {
        RecastConfig config = RecastConfiguration.getConfig();
        InputGeomProvider geomProvider = new SimpleInputGeomProvider(getVertices(sourceMesh), getIndices(sourceMesh));
        float[] minBounds = geomProvider.getMeshBoundsMin();
        float[] maxBounds = geomProvider.getMeshBoundsMax();
        RecastBuilderConfig builderConfig = new RecastBuilderConfig(config, minBounds, maxBounds);
        Context context = new Context();

        // Step 2. Rasterize input polygon soup.
        // Allocate voxel heightfield where we rasterize our input data to.
        Heightfield heightfield = new Heightfield(builderConfig.width, builderConfig.height, builderConfig.bmin,
                builderConfig.bmax, config.cs, config.ch);

        for (TriMesh geom : geomProvider.meshes()) {
            float[] verts = geom.getVerts();
            int[] tris = geom.getTris();
            int ntris = tris.length / 3;
            // Allocate array that can hold triangle area types.
            // If you have multiple meshes you need to process, allocate
            // and array which can hold the max number of triangles you need to
            // process.

            // Find triangles which are walkable based on their slope and rasterize
            // them.
            // If your input data is multiple meshes, you can transform them here,
            // calculate
            // the are type for each of the meshes and rasterize them.
            int[] m_triareas = Recast.markWalkableTriangles(context, config.walkableSlopeAngle, verts, tris, ntris,
                    RecastConfiguration.SAMPLE_AREAMOD_GROUND);
            RecastRasterization.rasterizeTriangles(context, verts, tris, m_triareas, ntris, heightfield,
                    config.walkableClimb);
        }

        // Step 3. Filter walkables surfaces.
        // Once all geometry is rasterized, we do initial pass of filtering to
        // remove unwanted overhangs caused by the conservative rasterization
        // as well as filter spans where the character cannot possibly stand.
        RecastFilter.filterLowHangingWalkableObstacles(context, config.walkableClimb, heightfield);
        RecastFilter.filterLedgeSpans(context, config.walkableHeight, config.walkableClimb, heightfield);
        RecastFilter.filterWalkableLowHeightSpans(context, config.walkableHeight, heightfield);

        // Step 4. Partition walkable surface to simple regions.
        // Compact the heightfield so that it is faster to handle from now on.
        // This will result more cache coherent data as well as the neighbours
        // between walkable cells will be calculated.
        CompactHeightfield compactHeightfield = Recast.buildCompactHeightfield(context, config.walkableHeight,
                config.walkableClimb, heightfield);

        // Erode the walkable area by agent radius.
        RecastArea.erodeWalkableArea(context, config.walkableRadius, compactHeightfield);

        // (Optional) Mark areas.
        /*
         * ConvexVolume vols = m_geom->getConvexVolumes(); for (int i = 0; i <
         * m_geom->getConvexVolumeCount(); ++i) rcMarkConvexPolyArea(context,
         * vols[i].verts, vols[i].nverts, vols[i].hmin, vols[i].hmax, (unsigned
         * char)vols[i].area, *compactHeightfield);
         */

        // Partition the heightfield so that we can use simple algorithm later
        // to triangulate the walkable areas.
        // There are 3 martitioning methods, each with some pros and cons:
        // 1) Watershed partitioning
        // - the classic Recast partitioning
        // - creates the nicest tessellation
        // - usually slowest
        // - partitions the heightfield into nice regions without holes or
        // overlaps
        // - the are some corner cases where this method creates produces holes
        // and overlaps
        // - holes may appear when a small obstacles is close to large open area
        // (triangulation can handle this)
        // - overlaps may occur if you have narrow spiral corridors (i.e
        // stairs), this make triangulation to fail
        // * generally the best choice if you precompute the nacmesh, use this
        // if you have large open areas
        // 2) Monotone partioning
        // - fastest
        // - partitions the heightfield into regions without holes and overlaps
        // (guaranteed)
        // - creates long thin polygons, which sometimes causes paths with
        // detours
        // * use this if you want fast navmesh generation
        // 3) Layer partitoining
        // - quite fast
        // - partitions the heighfield into non-overlapping regions
        // - relies on the triangulation code to cope with holes (thus slower
        // than monotone partitioning)
        // - produces better triangles than monotone partitioning
        // - does not have the corner cases of watershed partitioning
        // - can be slow and create a bit ugly tessellation (still better than
        // monotone)
        // if you have large open areas with small obstacles (not a problem if
        // you use tiles)
        // * good choice to use for tiled navmesh with medium and small sized
        // tiles

        if (config.partitionType == PartitionType.WATERSHED) {
            // Prepare for region partitioning, by calculating distance field
            // along the walkable surface.
            RecastRegion.buildDistanceField(context, compactHeightfield);
            // Partition the walkable surface into simple regions without holes.
            RecastRegion.buildRegions(context, compactHeightfield, borderSize, config.minRegionArea,
                    config.mergeRegionArea);
        } else if (config.partitionType == PartitionType.MONOTONE) {
            // Partition the walkable surface into simple regions without holes.
            // Monotone partitioning does not need distancefield.
            RecastRegion.buildRegionsMonotone(context, compactHeightfield, borderSize, config.minRegionArea,
                    config.mergeRegionArea);
        } else {
            // Partition the walkable surface into simple regions without holes.
            RecastRegion.buildLayerRegions(context, compactHeightfield, borderSize, config.minRegionArea);
        }

        // Step 5. Trace and simplify region contours.
        // Create contours.
        // ContourSet m_cset = RecastContour.buildContours(context, compactHeightfield,
        // config.maxSimplificationError, config.maxEdgeLen,
        // RecastConstants.RC_CONTOUR_TESS_WALL_EDGES);
        ContourSet m_cset = RecastContour.buildContours(context, compactHeightfield, config.maxSimplificationError,
                config.maxEdgeLen, 0x01);
        // Step 6. Build polygons mesh from contours.
        // Build polygon navmesh from the contours.
        PolyMesh m_pmesh = RecastMesh.buildPolyMesh(context, m_cset, config.maxVertsPerPoly);

        // Step 7. Create detail mesh which allows to access approximate height
        // on each polygon.
        PolyMeshDetail m_dmesh = RecastMeshDetail.buildPolyMeshDetail(context, m_pmesh, compactHeightfield,
                config.detailSampleDist, config.detailSampleMaxError);
        // String exportFilename = "assets\\navmesh.";
        String exportFilename = "navmesh.obj";
        // exportObj(exportFilename.substring(0, exportFilename.lastIndexOf('.')) +
        // "_debug.obj", m_dmesh);
        exportObj(exportFilename, m_dmesh);

        // must set flags for navigation controls to work
        for (int i = 0; i < m_pmesh.npolys; ++i) {
            m_pmesh.flags[i] = RecastConfiguration.SAMPLE_POLYFLAGS_WALK;
        }
        NavMeshDataCreateParams params = new NavMeshDataCreateParams();

        params.verts = m_pmesh.verts;
        params.vertCount = m_pmesh.nverts;
        params.polys = m_pmesh.polys;
        params.polyAreas = m_pmesh.areas;
        params.polyFlags = m_pmesh.flags;
        params.polyCount = m_pmesh.npolys;
        params.nvp = m_pmesh.nvp;
        params.detailMeshes = m_dmesh.meshes;
        params.detailVerts = m_dmesh.verts;
        params.detailVertsCount = m_dmesh.nverts;
        params.detailTris = m_dmesh.tris;
        params.detailTriCount = m_dmesh.ntris;
        params.walkableHeight = config.walkableHeight;
        params.walkableRadius = config.walkableRadius;
        params.walkableClimb = config.walkableClimb;
        params.bmin = m_pmesh.bmin;
        params.bmax = m_pmesh.bmax;
        params.cs = config.cs;
        params.ch = config.ch;
        params.buildBvTree = true;

        MeshData meshData = NavMeshBuilder.createNavMeshData(params);
        NavMesh navMesh = new NavMesh(meshData, params.nvp, 0);
        // TODO later rework it to return NavMesh. For now, just try to test if works
        // with existing object
        // return navMesh
    }

    /**
     * Get vertcices of a mesh boxed to Float.
     *
     * @param mesh
     * @return Returns boxed List of vertices.
     */
    private static List<Float> getVertices(Mesh mesh) {
        FloatBuffer buffer = mesh.getFloatBuffer(VertexBuffer.Type.Position);
        float[] vertexArray = BufferUtils.getFloatArray(buffer);
        List<Float> vertexList = new ArrayList<>();

        for (float vertex : vertexArray) {
            vertexList.add(vertex);
        }
        return vertexList;
    }

    /**
     * Get all triangles from a mesh boxed to Integer.
     *
     * @param mesh
     * @return Returns boxed List of triangles.
     */
    private static List<Integer> getIndices(Mesh mesh) {
        int[] indices = new int[3];
        Integer[] triangles = new Integer[mesh.getTriangleCount() * 3];

        for (int i = 0; i < triangles.length; i += 3) {
            mesh.getTriangle(i / 3, indices);
            triangles[i] = indices[0];
            triangles[i + 1] = indices[1];
            triangles[i + 2] = indices[2];
        }
        // Independent copy so Arrays.asList is garbage collected
        List<Integer> indicesList = new ArrayList<>(Arrays.asList(triangles));
        return indicesList;
    }

    // Denis: redefined as parameters were not matching before
    private void exportObj(String filename, PolyMeshDetail dmesh) {

        File file = new File(filename);
        try (FileWriter out = new FileWriter(file)) {
            // vertex
            for (int v = 0; v < dmesh.nverts; v++) {
                out.write(
                        "v " + dmesh.verts[v * 3] + " " + dmesh.verts[v * 3 + 1] + " " + dmesh.verts[v * 3 + 2] + "\n");
            }
            // vertex normal
            Vector3f[] vector3Array = getVector3Array(dmesh);
            for (int m = 0; m < dmesh.nmeshes; m++) {
                int vfirst = dmesh.meshes[m * 4];
                int tfirst = dmesh.meshes[m * 4 + 2];
                for (int f = 0; f < dmesh.meshes[m * 4 + 3]; f++) {
                    Vector3f normal = Triangle.computeTriangleNormal(
                            vector3Array[(vfirst + dmesh.tris[(tfirst + f) * 4])],
                            vector3Array[(vfirst + dmesh.tris[(tfirst + f) * 4 + 1])],
                            vector3Array[(vfirst + dmesh.tris[(tfirst + f) * 4 + 2])], null);
                    out.write("vn " + normal.x + " " + normal.y + " " + normal.z + "\n");
                }
            }
            // face
            int count = 1;
            int offset = 0;
            for (int m = 0; m < dmesh.nmeshes; m++) {
                int vfirst = dmesh.meshes[m * 4];
                int tfirst = dmesh.meshes[m * 4 + 2];
                for (int f = 0; f < dmesh.meshes[m * 4 + 3]; f++) {
                    out.write("f " + ((vfirst + dmesh.tris[(tfirst + f) * 4] + 1) + offset) + "//" + count + " "
                            + ((vfirst + dmesh.tris[(tfirst + f) * 4 + 1] + 1) + offset) + "//" + count + " "
                            + ((vfirst + dmesh.tris[(tfirst + f) * 4 + 2] + 1) + offset) + "//" + count + "\n");
                    count++;
                }
            }
            offset += dmesh.nverts;
            out.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private Vector3f[] getVector3Array(PolyMeshDetail dmesh) {
        Vector3f[] vector3Array = new Vector3f[dmesh.nverts];
        for (int i = 0; i < dmesh.nverts; i++) {
            vector3Array[i] = new Vector3f(dmesh.verts[i * 3], dmesh.verts[i * 3 + 1], dmesh.verts[i * 3 + 2]);
        }
        return vector3Array;
    }
}