package com.mesearment.com;

import io.realm.RealmObject;

public class RecordModel extends RealmObject {

    String notes, measuredData, Measuredunit;

    RecordModel(String notes, String Data, String Unit) {
        this.notes = notes;
        this.measuredData = Data;
        this.Measuredunit = Unit;
    }

    public RecordModel() {
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getMeasuredData() {
        return measuredData;
    }

    public void setMeasuredData(String measuredData) {
        this.measuredData = measuredData;
    }

    public String getMeasuredunit() {
        return Measuredunit;
    }

    public void setMeasuredunit(String measuredunit) {
        Measuredunit = measuredunit;
    }
}
