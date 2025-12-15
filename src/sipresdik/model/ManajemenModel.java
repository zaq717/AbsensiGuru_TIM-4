/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sipresdik.model;

public class ManajemenModel {
    
    private int id;
    private String Username, Password;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public String getusername(){
        return Username;
    }
    public void setusername(String username){
        this.Username = username;
    }
    
    public String getpassword(){
        return Password;
    }
    public void setpassword(String password){
        this.Password = password;
    }
}
