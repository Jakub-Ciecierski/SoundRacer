package com.example.mini.game.util;

/**
 * Class has huge constraints i.e.:<p/>
 * >> it loads only vertices coordinates and faces drawing order<p/>
 * >> faces coordinates must be length 3 i.e all face must be a triangle.
 * <p><p/>
 * When we consider Blender program, all faces must be 'beautify' and no textures,
 * lights, animation etc. can be applied. Just a raw model.
 * <p></p>
 * Created by dybisz on 2014-12-11.
 */
public class ObjModel {
    /**
     * Vertex prefix in .obj standard.
     */
    private static final String VERTEX = "v";
    /**
     * Face prefix in .obj standard.
     */
    private static final String FACE = "f";
    /**
     * Keeps loaded .obj file as a String object.
     */
    private String rawModelData;
    /**
     * Array for vertices coordinates.
     */
    private float[] vertices;
    /**
     * Array for faces drawing order.
     */
    private float[] faces;

    /**
     * Method loads parse .obj file int o a String object.
     *
     * @param objName Name of an .obj file to process.
     *                Given file must be in models folder.
     */
    public String loadObjModel(String objName) {
        // loading to currentObjModel
        return null;
    }

    /**
     * Loads vertices coordinates from, previously converted to String object, .obj.
     *
     * @param objAsAString {@link #rawModelData} is commonly used.
     * @return An array with vertices information.
     */
    public float[] extractVerticesInformation(String objAsAString) {

        return null;
    }

    /**
     * Loads drawing order of faces from, previously converted to String, object .obj.
     *
     * @param objAsAString {@link #rawModelData} is commonly used.
     * @return An array with faces draw order information.
     */
    public float[] extractFacesInformation(String objAsAString) {
        return null;
    }

    /**
     * Main constructor.
     *
     * @param objName Name of an .obj file to load.
     */
    public ObjModel(String objName) {
        rawModelData = loadObjModel(objName);
        vertices = extractVerticesInformation(rawModelData);
        faces = extractFacesInformation(rawModelData);
    }
}
