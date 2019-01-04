package com.automata.calculatorkenya;

class Model {
    private String name;
    private int image;

    public Model(){
    }

    public Model(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Model{" +
                "name='" + name + '\'' +
                ", image=" + image +
                '}';
    }
}
