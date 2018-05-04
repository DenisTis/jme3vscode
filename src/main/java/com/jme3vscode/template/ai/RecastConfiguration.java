package com.jme3vscode.template.ai;

import org.recast4j.recast.AreaModification;
import org.recast4j.recast.RecastConfig;
import org.recast4j.recast.RecastConstants.PartitionType;

/* Navigation using Recast
Check this thread: https://hub.jmonkeyengine.org/t/navmesh-cell-size/39052/7
And this for some comments from JME: https://jmonkeyengine.github.io/wiki/jme3/advanced/recast.html
*/
public class RecastConfiguration {
    // Some constants which are not present at recast package and are taken from
    // public class SampleAreaModifications { from git of recast4j
    public static int SAMPLE_POLYAREA_TYPE_MASK = 0x07;
    public static int SAMPLE_POLYAREA_TYPE_GROUND = 0x1;
    public static int SAMPLE_POLYAREA_TYPE_WATER = 0x2;
    public static int SAMPLE_POLYAREA_TYPE_ROAD = 0x3;
    public static int SAMPLE_POLYAREA_TYPE_DOOR = 0x4;
    public static int SAMPLE_POLYAREA_TYPE_GRASS = 0x5;
    public static int SAMPLE_POLYAREA_TYPE_JUMP = 0x6;

    public static AreaModification SAMPLE_AREAMOD_GROUND = new AreaModification(SAMPLE_POLYAREA_TYPE_GROUND,
            SAMPLE_POLYAREA_TYPE_MASK);
    public static AreaModification SAMPLE_AREAMOD_WATER = new AreaModification(SAMPLE_POLYAREA_TYPE_WATER,
            SAMPLE_POLYAREA_TYPE_MASK);
    public static AreaModification SAMPLE_AREAMOD_ROAD = new AreaModification(SAMPLE_POLYAREA_TYPE_ROAD,
            SAMPLE_POLYAREA_TYPE_MASK);
    public static AreaModification SAMPLE_AREAMOD_GRASS = new AreaModification(SAMPLE_POLYAREA_TYPE_GRASS,
            SAMPLE_POLYAREA_TYPE_MASK);
    public static AreaModification SAMPLE_AREAMOD_DOOR = new AreaModification(SAMPLE_POLYAREA_TYPE_DOOR,
            SAMPLE_POLYAREA_TYPE_DOOR);
    public static AreaModification SAMPLE_AREAMOD_JUMP = new AreaModification(SAMPLE_POLYAREA_TYPE_JUMP,
            SAMPLE_POLYAREA_TYPE_JUMP);

    public static final int SAMPLE_POLYFLAGS_WALK = 0x01; // Ability to walk (ground, grass, road)
    public static final int SAMPLE_POLYFLAGS_SWIM = 0x02; // Ability to swim (water).
    public static final int SAMPLE_POLYFLAGS_DOOR = 0x04; // Ability to move through doors.
    public static final int SAMPLE_POLYFLAGS_JUMP = 0x08; // Ability to jump.
    public static final int SAMPLE_POLYFLAGS_DISABLED = 0x10; // Disabled polygon
    public static final int SAMPLE_POLYFLAGS_ALL = 0xffff; // All abilities.

    // Partition the heightfield so that we can use simple algorithm later to
    // triangulate the walkable areas.
    // There are 3 martitioning methods, each with some pros and cons:
    // 1) Watershed partitioning
    // - the classic Recast partitioning
    // - creates the nicest tessellation
    // - usually slowest
    // - partitions the heightfield into nice regions without holes or overlaps
    // - the are some corner cases where this method creates produces holes and
    // overlaps
    // - holes may appear when a small obstacles is close to large open area
    // (triangulation can handle this)
    // - overlaps may occur if you have narrow spiral corridors (i.e stairs), this
    // make triangulation to fail
    // * generally the best choice if you precompute the nacmesh, use this if you
    // have large open areas
    // 2) Monotone partioning
    // - fastest
    // - partitions the heightfield into regions without holes and overlaps
    // (guaranteed)
    // - creates long thin polygons, which sometimes causes paths with detours
    // * use this if you want fast navmesh generation
    private final static PartitionType partitionType = PartitionType.MONOTONE;
    // private final static AreaModification = new AreaModification(1);
    // The width/height size of tile's on the xz-plane.
    // >=0
    private final static int tileSize = 64;
    // The size of the non-navigable border around the heightfield.
    // >=0
    private final static int borderSize = 0;
    // The width and depth resolution used when sampling the source geometry. The
    // width and depth of the cell columns that make up voxel fields.
    // Cells are laid out on the width/depth plane of voxel fields. Width is
    // associated with the x-axis of the source geometry. Depth is associated
    // with the z-axis.
    // A lower value allows for the generated mesh to more closely match the
    // source geometry, but at a higher processing and memory cost.
    // Small cell size needed to allow mesh to travel up stairs.
    // Adjust m_cellSize and m_cellHeight for contour simplification exceptions.
    // > 0, outdoors = m_agentRadius/2, indoors = m_agentRadius/3, m_cellSize =
    // m_agentRadius for very small cells.
    private final static float cellSize = 0.25f;
    // Height is associated with the y-axis of the source geometry.
    // A smaller value allows for the final mesh to more closely match the source
    // geometry at a potentially higher processing cost. (Unlike cellSize, using
    // a lower value for cellHeight does not significantly increase memory use.)
    // This is a core configuration value that impacts almost all other
    // parameters.
    // m_agentHeight, m_agentMaxClimb, and m_detailSampleMaxError will
    // need to be greater than this value in order to function correctly.
    // m_agentMaxClimb is especially susceptible to impact from the value of
    // m_cellHeight.
    // > 0, m_cellSize/2
    private final static float cellHeight = 0.125f;
    // Represents the minimum floor to ceiling height that will still allow the
    // floor area to be considered traversable. It permits detection of overhangs
    // in the geometry that make the geometry below become un-walkable. It can
    // also be thought of as the maximum agent height.
    // This value should be at least two times the value of m_cellHeight in order
    // to get good results.
    // > 0
    private final static float agentHeight = 2.0f;
    // Represents the closest any part of a mesh can get to an obstruction in the
    // source geometry.
    // Usually this value is set to the maximum bounding radius of agents
    // utilizing the meshes for navigation decisions.
    // This value must be greater than the m_cellSize to have an effect.
    // >= 0
    private final static float agentRadius = 0.5f;
    // Represents the maximum ledge height that is considered to still be
    // traversable.
    // Prevents minor deviations in height from improperly showing as
    // obstructions. Permits detection of stair-like structures, curbs, etc.
    // m_agentMaxClimb should be greater than two times m_cellHeight.
    // (m_agentMaxClimb > m_cellHeight * 2) Otherwise the resolution of the voxel
    // field may not be high enough to accurately detect traversable ledges.
    // Ledges may merge, effectively doubling their step height. This is
    // especially an issue for stairways.
    // >= 0, m_agentMaxClimb/m_cellHeight = voxels.
    private final static float agentMaxClimb = 0.7f;
    // The maximum slope that is considered traversable. (In degrees.)
    // >= 0
    private final static float agentMaxSlope = 50.0f;
    // The minimum region size for unconnected (island) regions.
    // >= 0
    private final static int regionMinSize = 8;
    // Any regions smaller than this size will, if possible, be merged with
    // larger regions.
    // >= 0
    private final static int regionMergeSize = 20;
    // The maximum length of polygon edges that represent the border of meshes.
    // Adjust to decrease dangling errors.
    // >= 0, m_agentRadius * 8
    private final static float edgeMaxLen = 6.0f;
    // The maximum distance the edges of meshes may deviate from the source
    // geometry.
    // A lower value will result in mesh edges following the xz-plane geometry
    // contour more accurately at the expense of an increased triangle count.
    // >= 0, 1.1 to 1.5 for best results.
    private final static float edgeMaxError = 1.3f;
    // The maximum number of vertices per polygon for polygons generated during
    // the voxel to polygon conversion process.
    // >= 3
    private final static int vertsPerPoly = 6;
    // Sets the sampling distance to use when matching the detail mesh to the
    // surface of the original geometry.
    // Higher values result in a detail mesh that conforms more closely to the
    // original geometry's surface at the cost of a higher final triangle count
    // and higher processing cost.
    // The difference between this parameter and m_edgeMaxError is that this
    // parameter operates on the height rather than the xz-plane. It also matches
    // the entire detail mesh surface to the contour of the original geometry.
    // m_edgeMaxError only matches edges of meshes to the contour of the original
    // geometry.
    // Decrease to reduce dangling errors.
    // >= 0
    private final static float detailSampleDist = 5.0f;
    // The maximum distance the surface of the detail mesh may deviate from the
    // surface of the original geometry.
    // Increase to reduce dangling errors.
    // >= 0
    private final static float detailSampleMaxError = 5.0f;

    private static RecastConfig config = null;

    public static RecastConfig getConfig() {
        if (config == null) {
            // TODO Area modification is area id - most probably if mesh can have different
            // areas
            // check what my mesh will have or how to set up them
            // here Modification Area is defined - check how it is done on mesh and
            // implement too
            // https://github.com/ppiastucki/recast4j/blob/master/recast/src/test/java/org/recast4j/recast/SampleAreaModifications.java
            config = new RecastConfig(PartitionType.MONOTONE, cellSize, cellHeight, agentHeight, agentRadius,
                    agentMaxClimb, agentMaxSlope, regionMinSize, regionMergeSize, edgeMaxLen, edgeMaxError,
                    vertsPerPoly, detailSampleDist, detailSampleMaxError, tileSize, SAMPLE_AREAMOD_GROUND);
        }
        return config;
    }

}