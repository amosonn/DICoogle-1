/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.ua.dicoogle.core;

/**
 *
 * @author carloscosta
 */
public class QueryResultRecord {
    private String patientName;
    private String PatientID;
    private String Modality;
    private String StudyDate;
    private String FilePath;
    private byte[] Thumbnail;

    public QueryResultRecord(String patientName, String PatientID, 
                             String Modality, String StudyDate, 
                             String FilePath, byte[] Thumbnail){
        this.patientName = patientName;
        this.PatientID = PatientID;
        this.Modality = Modality;
        this.StudyDate = StudyDate;
        this.FilePath = FilePath;
        this.Thumbnail = Thumbnail;
    }
    
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientID() {
        return PatientID;
    }

    public void setPatientID(String PatientID) {
        this.PatientID = PatientID;
    }

    public String getModality() {
        return Modality;
    }

    public void setModality(String Modality) {
        this.Modality = Modality;
    }

    public String getStudyDate() {
        return StudyDate;
    }

    public void setStudyDate(String StudyDate) {
        this.StudyDate = StudyDate;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String FilePath) {
        this.FilePath = FilePath;
    }

    public byte[] getThumbnail() {
        return Thumbnail;
    }
        
}
