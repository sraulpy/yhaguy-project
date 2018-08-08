package com.yhaguy.util.migracion.baterias;

import com.coreweb.extras.csv.CSV;
import com.yhaguy.domain.ArticuloListaPrecio;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.RegisterDomain;

public class BAT_Clientes_ListaPrecio {
	
	static String src = "./WEB-INF/docs/Baterias/clientes_listaprecio.csv";
	
	static String[][] cab = { { "Empresa", CSV.STRING } };
	static String[][] det = { { "CLIENTE", CSV.STRING }, { "LISTA", CSV.STRING } };
	
	/**
	 * pobla las empresas..
	 */
	public static void setListaPrecio() throws Exception {
		
		RegisterDomain rr = RegisterDomain.getInstance();
		CSV csv = new CSV(cab, det, src);
		
		ArticuloListaPrecio distribuidor = rr.getListaDePrecio(1);
		ArticuloListaPrecio mayorista = rr.getListaDePrecio(2);

		csv.start();
		while (csv.hashNext()) {

			String cliente = csv.getDetalleString("CLIENTE");
			String lista = csv.getDetalleString("LISTA");
			ArticuloListaPrecio lprecio = lista.equals("distribuidor") ? distribuidor : mayorista;
			
			Cliente cli = rr.getClienteByRazonSocial(cliente);
			if (cli != null) {
				cli.setListaPrecio(lprecio);
				rr.saveObject(cli, cli.getUsuarioMod());
				
				System.out.println(cliente + " - " + lprecio.getDescripcion());
			}
 			
		}
	}
	
	public static void main(String[] args) {
		try {
			BAT_Clientes_ListaPrecio.setListaPrecio();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
