package es.udc.psi14.lab07trabazo;

public class Notas {

    private int id;
    private String nombre;
    private String apellido;
    private String materia;
    private boolean mencion;
    private float nota;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public boolean getMencion() {
        return mencion;
    }

    public void setMencion(boolean mencion) {
        this.mencion = mencion;
    }

    public float getNota() {
        return nota;
    }

    public void setNota(float nota) {
        this.nota = nota;
    }
}
