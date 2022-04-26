/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * , open the template in the editor.
 */
package Services;
import Entities.Utilisateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import Util.MyDB;

/**
 *
 * @author wajdi
 */

public class ServiceUtilisateur implements Iservice<Utilisateur> {
    Connection connection;
    public ServiceUtilisateur() {
        connection = MyDB.getInstance().getConnection();
    }
    public boolean login(String username, String input){
        try{
            Utilisateur t = this.GetByUsername(username);
            return Utilisateur.verifyUserPassword(input, t.getPassword(), t.getSalt());
        }catch (Exception e){
            System.out.println(e.getStackTrace());
            return false;
        }
    }

    @Override
    public void ajouter(Utilisateur t) throws Exception{
        try {
            String req1 = "insert into Utilisateur(cin,email,username,password,name,prenom,date_naissance,image,tel) values (?,?,?,?,?,?,?,?,?,?,?)";

            if(this.GetByUsername(t.getUsername()) != null) throw new Exception("Username est déja utilisé !");
            else{
            PreparedStatement ps = connection.prepareStatement(req1);
            ps.setInt(1, t.getCin());
            ps.setString(2, t.getEmail());
            ps.setString(3, t.getUsername());
            ps.setString(4, t.getPassword());
            ps.setString(5, t.getName());
            ps.setString(6, t.getPrenom());
            ps.setDate(7,  t.getDate_naissance());
            ps.setString(8, t.getImage());
            ps.setString(9, t.getTel());
            ps.setString(10, t.getSalt());
            ps.setString(11, t.getRoles().toString());
            ps.executeUpdate();}
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void modifier( Utilisateur t) {

        try {
            String req = "UPDATE  Utilisateur SET  email= ?, username= ?, password= ?, roles= ?, name = ? , prenom = ? , date_naissance = ? , image = ? , tel = ?  where cin = ?";
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setString(1,t.getEmail() );
            ps.setString(2, t.getUsername());
            ps.setString(3, t.getPassword());
            ps.setString(4,  t.getRoles().toString());
            ps.setString(5, t.getName());
            ps.setString(6, t.getPrenom());
            ps.setDate(7,t.getDate_naissance() );
            ps.setString(8,t.getImage() );
            ps.setString(9,t.getTel() );
            ps.setInt(10,t.getCin() );
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Utilisateur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void supprimer(int cin) {
        try {
            String req= "delete from Utilisateur where cin = ?";
            PreparedStatement st = connection.prepareStatement(req);
            st.setInt(1, cin);

            st.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Utilisateur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<Utilisateur> recuperer() {
        List<Utilisateur> list =new ArrayList<>();
        try {
            String req = "select * from Utilisateur";
            Statement st = connection.createStatement();
            ResultSet rs =st.executeQuery(req);

            while(rs.next()){
                Utilisateur l = new Utilisateur();
                l.setCin(rs.getInt("cin"));
                l.setName(rs.getString("name"));
                l.setPrenom(rs.getString(3));
                l.setEmail(rs.getString("email"));
                l.setUsername((rs.getString("username")));
                l.setPassword(rs.getString("password"));
                Array roles = rs.getArray("roles");
                l.setSalt(rs.getString("salt"));
                l.setRoles((String[])roles.getArray());
                l.setDate_naissance(rs.getDate("date_naissance"));
                l.setImage(rs.getString("image"));
                l.setTel(rs.getString("tel"));
                list.add(l);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return list;
    }
    public Utilisateur GetByUsername(String username) {
        return recuperer().stream().filter(e -> e.getUsername() == username).findFirst().get();
    }
}



