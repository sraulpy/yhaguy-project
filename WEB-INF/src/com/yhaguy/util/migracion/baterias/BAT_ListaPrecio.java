package com.yhaguy.util.migracion.baterias;

import com.coreweb.extras.csv.CSV;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloListaPrecio;
import com.yhaguy.domain.ArticuloListaPrecioDetalle;
import com.yhaguy.domain.RegisterDomain;

public class BAT_ListaPrecio {

	static RegisterDomain rr = RegisterDomain.getInstance();
	
	static String src = "./WEB-INF/docs/Baterias/ListaPrecioBaterias.csv";
	static String srcOferta = "./WEB-INF/docs/Baterias/ListaPrecioBateriasOferta.csv";
	static String srcOfertaCred = "./WEB-INF/docs/Baterias/ListaPrecioBateriasOferta.csv";
	
	String[][] cab = { { "Empresa", CSV.STRING } };
	String[][] det = { { "CODIGO", CSV.STRING },
			{ "DISTRIBUIDOR", CSV.STRING }, { "MAYORISTA", CSV.STRING },
			{ "AUTOCENTROS", CSV.STRING } };
	String[][] detOferta = {{ "CODIGO", CSV.STRING }, {"OFERTA", CSV.STRING}, {"CREDITO", CSV.STRING}};
	
	public void poblarPrecios() throws Exception {
		CSV csv = new CSV(cab, det, src);
		csv.start();
		
		ArticuloListaPrecio distribuidor = (ArticuloListaPrecio) rr.getObject(ArticuloListaPrecio.class.getName(), 1);
		ArticuloListaPrecio mayorista = (ArticuloListaPrecio) rr.getObject(ArticuloListaPrecio.class.getName(), 2);
		ArticuloListaPrecio autocentros = (ArticuloListaPrecio) rr.getObject(ArticuloListaPrecio.class.getName(), 3);
		
		while (csv.hashNext()) {
			
			String codigo = csv.getDetalleString("CODIGO");
			double distribuidor_ = Double.parseDouble(csv.getDetalleString("DISTRIBUIDOR").replace(".", "").replace(",", ""));
			double mayorista_ = Double.parseDouble(csv.getDetalleString("MAYORISTA").replace(".", "").replace(",", ""));
			double autocentros_ = Double.parseDouble(csv.getDetalleString("AUTOCENTROS").replace(".", "").replace(",", ""));
			
			Articulo art = rr.getArticulo(codigo);
			
			if (art != null) {
				ArticuloListaPrecioDetalle detDist = new ArticuloListaPrecioDetalle();
				detDist.setActivo(true);
				detDist.setPrecioGs_contado(distribuidor_);
				detDist.setArticulo(art);
				distribuidor.getDetalles().add(detDist);
				
				ArticuloListaPrecioDetalle detMay = new ArticuloListaPrecioDetalle();
				detMay.setActivo(true);
				detMay.setPrecioGs_contado(mayorista_);
				detMay.setArticulo(art);
				mayorista.getDetalles().add(detMay);
				
				ArticuloListaPrecioDetalle detAut = new ArticuloListaPrecioDetalle();
				detAut.setActivo(true);
				detAut.setPrecioGs_contado(autocentros_);
				detAut.setArticulo(art);
				autocentros.getDetalles().add(detAut);
				
				System.out.println("encontrado: " + codigo);
			} else {
				System.err.println("no encontrado: " + codigo);
			}			
		}
		rr.saveObject(distribuidor, "sys");
		rr.saveObject(mayorista, "sys");
		rr.saveObject(autocentros, "sys");
	}	

	public void poblarPreciosOferta() throws Exception {
		CSV csv = new CSV(cab, detOferta, srcOferta);
		csv.start();
		
		ArticuloListaPrecio oferta = (ArticuloListaPrecio) rr.getObject(ArticuloListaPrecio.class.getName(), 6);
		
		while (csv.hashNext()) {
			
			String codigo = csv.getDetalleString("CODIGO");
			double oferta_ = Double.parseDouble(csv.getDetalleString("OFERTA").replace(".", "").replace(",", ""));
			double credito_ = Double.parseDouble(csv.getDetalleString("CREDITO").replace(".", "").replace(",", ""));
			
			Articulo art = rr.getArticulo(codigo);
			
			if (art != null) {
				ArticuloListaPrecioDetalle detOfert = new ArticuloListaPrecioDetalle();
				detOfert.setActivo(true);
				detOfert.setPrecioGs_contado(oferta_);
				detOfert.setPrecioGs_credito(credito_);
				detOfert.setArticulo(art);
				oferta.getDetalles().add(detOfert);
				
				System.out.println("encontrado: " + codigo);
			} else {
				System.err.println("no encontrado: " + codigo);
			}			
		}
		rr.saveObject(oferta, "sys");
	}
	
	
	public static void main(String[] args) {
		try {
			BAT_ListaPrecio test = new BAT_ListaPrecio();
			test.poblarPrecios();
			//test.poblarPreciosOferta();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
