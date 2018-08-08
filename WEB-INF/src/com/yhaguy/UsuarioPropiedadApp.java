package com.yhaguy;

import java.util.ArrayList;
import java.util.List;

import com.coreweb.UsuarioPropiedad;
import com.coreweb.login.LoginUsuarioDTO;
import com.coreweb.util.MyPair;

/**
 * Contiene las propiedades especificas de los usuarios
 * 
 * @author daniel
 * 
 */
public class UsuarioPropiedadApp extends UsuarioPropiedad {

	public UsuarioPropiedadApp(LoginUsuarioDTO usuario) {
		super(usuario);
		// TODO Auto-generated constructor stub
	}

	/**
	 * El límite de compra para un usuario, sin requerirle autorización. Si no
	 * tiene asigando, retorna cero.
	 * 
	 * @return
	 */
	public double getLimiteCompra() {
		double out = 0;
		try {
			out = this.getUsuario().getPropiedadDouble(
					Configuracion.USUARIO_LIMITE_COMPRA);
		} catch (Exception e) {

		}
		return out;
	}

	/**
	 * 
	 * La lista de depositos habilitados para recepcion
	 * 
	 * @return
	 */
	public List<MyPair> getDepositosHabEntrada(UtilDTO util) throws Exception {
		List<String> out = new ArrayList<String>();
		List<MyPair> result = new ArrayList<MyPair>();

		out = this.getUsuario().getPropiedades(
				Configuracion.USUARIO_DEPOSITOS_HAB_ENTRADA);
		List<MyPair> depositos = util.getDepositosMyPair();
		for (MyPair mp : depositos) {
			for (String s : out) {
				if (mp.getText().compareTo(s) == 0) {
					result.add(mp);
				}
			}
		}
		/*
		for (MyPair mp : result) {
			System.out.println("-----" + mp.getText() + "-----");
		}
		*/
		return result;
	}

	/**
	 * 
	 * La lista de depositos habilitados para envio
	 * 
	 * @return
	 */
	public List<MyPair> getDepositosHabSalida(UtilDTO util) throws Exception {
		List<String> out = new ArrayList<String>();
		List<MyPair> result = new ArrayList<MyPair>();

		out = this.getUsuario().getPropiedades(
				Configuracion.USUARIO_DEPOSITOS_HAB_SALIDA);
		List<MyPair> depositos = util.getDepositosMyPair();
		for (MyPair mp : depositos) {
			for (String s : out) {
				if (mp.getText().compareTo(s) == 0) {
					result.add(mp);
				}
			}

		}

		return result;
	}
	
	
	/**
	 * El depósito habilitado del usuario para facturar..
	 */
	public MyPair getDepositoHabFacturar(UtilDTO util) throws Exception{
		MyPair out = new MyPair();
		
		String dep = this.getUsuario().getPropiedad(Configuracion.USUARIO_DEPOSITO_HAB_FACTURAR);
		for (MyPair d : util.getDepositosMyPair()) {
			if (d.getText().compareTo(dep) == 0) {
				out = d;
			}
		}		
		return out;
	}
	
	/**
	 * El modo de Venta seteado al usuario..
	 */
	public MyPair getModoVenta(List<MyPair> modoVentas) throws Exception{
		MyPair out = new MyPair();
	
		String modo = this.getUsuario().getPropiedad(Configuracion.USUARIO_MODO_VENTA);
		for (MyPair item : modoVentas) {
			if (item.getSigla().compareTo(modo) == 0) {
				out = item;
			}
		}		
		return out;
	}

}
