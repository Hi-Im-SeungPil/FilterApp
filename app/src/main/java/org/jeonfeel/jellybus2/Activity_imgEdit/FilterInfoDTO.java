package org.jeonfeel.jellybus2.Activity_imgEdit;


public class FilterInfoDTO {
    private float[] matrix;
    private String name;

    public FilterInfoDTO(float[] matrix, String name) {
        this.matrix = matrix;
        this.name = name;
    }

    public float[] getMatrix() {
        return matrix;
    }

    public void setMatrix(float[] matrix) {
        this.matrix = matrix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
