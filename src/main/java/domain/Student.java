package domain;

public class Student extends Entity<String> {
    private String firstName;
    private String lastName;
    private String guidingTeacher;
    private int group;
    private String email;

    public Student(String id, String firstName, String lastName, int group,String email,String guidingTeacher) {
        setId(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.group = group;
        this.email = email;
        this.guidingTeacher = guidingTeacher;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGuidingTeacher() {
        return guidingTeacher;
    }

    public void setGuidingTeacher(String guidingTeacher) {
        this.guidingTeacher = guidingTeacher;
    }

    @Override
    public String toString(){
        return  firstName + " " + lastName;
    }
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Student){
            Student st = (Student) obj;
            if(st == this)
                return true;

            if(st.getId().equals(getId()))
                return true;

            if(st.getFirstName().equals(firstName) && st.getLastName().equals(lastName) && st.getGuidingTeacher().equals(guidingTeacher) && st.getGroup() == group && st.getEmail().equals(email))
                return true;
        }
        return false;
    }
}