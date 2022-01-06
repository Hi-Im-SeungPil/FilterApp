package org.jeonfeel.jellybus2.Activity_imgEdit;

import androidx.annotation.Keep;

public class FilterDTOForFirebase {
    private String matrix;
    private String name;
    private long sequence;

    @Keep
    public FilterDTOForFirebase(){}

    public FilterDTOForFirebase(String matrix, String name, long sequence) {
        this.matrix = matrix;
        this.name = name;
        this.sequence = sequence;
    }

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
}
