package com.yhaguy.util.population;

import com.coreweb.domain.Register;
import com.coreweb.domain.Usuario;

public class DBPerfiles {
	
	public void test(){
		
		Register rr =  Register.getInstance();
		Usuario u = new Usuario();
		u.setNombre("Daniel Romero");
		u.setClave("clave");
		u.setLogin("danielr");
		try {
			rr.saveObject(u, "pepe");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	
	
	public static void main(String[] args) {
		DBPerfiles dbp = new DBPerfiles();
		dbp.test();
	}
}
