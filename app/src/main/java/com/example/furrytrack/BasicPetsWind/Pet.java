package com.example.furrytrack.BasicPetsWind;

import java.io.Serializable;
import java.util.Date;

public class Pet implements Serializable {
    private String name;
    private String type;
    private String gender;
    private Date birthDate;
    private Date adoptionDate;
    private String importantInfo;
    private Date lastVetVisit;
    private Date lastWalk;
    private Date lastWash;

    public Pet(String name,
               String type,
               String gender,
               Date birthDate,
               Date adoptionDate,
               String importantInfo,
               Date lastVetVisit,
               Date lastWalk,
               Date lastWash) {
        this.name = name;
        this.type = type;
        this.gender = gender;
        this.birthDate = birthDate;
        this.adoptionDate = adoptionDate;
        this.importantInfo = importantInfo;
        this.lastVetVisit = lastVetVisit;
        this.lastWalk = lastWalk;
        this.lastWash = lastWash;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public String getGender() { return gender; }
    public Date getBirthDate() { return birthDate; }
    public Date getAdoptionDate() { return adoptionDate; }
    public String getImportantInfo() { return importantInfo; }
    public Date getLastVetVisit() { return lastVetVisit; }
    public Date getLastWalk() { return lastWalk; }
    public Date getLastWash() { return lastWash; }
}