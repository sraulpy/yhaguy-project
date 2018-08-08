package com.yhaguy.util.migracion.mariano;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.coreweb.extras.csv.CSV;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.FacturaVentaMRA;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Venta;
import com.yhaguy.util.Utiles;

public class MigracionFacturasVenta {

	static RegisterDomain rr = RegisterDomain.getInstance();
	
	static String src = "./WEB-INF/docs/MRA/FACTURAS-VENTA.csv";
	
	static Map<Long, Long> VENDEDOR = new HashMap<Long, Long>();
	
	static final Object[] ID_WILFRIDO = { (long) 10, (long) 7 };
	static final Object[] ID_MIRIAN = { (long) 1, (long) 9 };
	static final Object[] ID_VICTOR = { (long) 2, (long) 8 };
	static final Object[] ID_EDGAR = { (long) 11, (long) 10 };
	static final Object[] ID_CARLOS = { (long) 9, (long) 11 };

	String[][] cab = { { "Empresa", CSV.STRING } };
	String[][] det = { { "NUMERO", CSV.STRING }, { "IDVENDEDOR", CSV.STRING } };
	
	/**
	 * 2  - Victor 	[8]
	 * 1  - Mirian 	[9]
	 * 10 - Wilfrido[7]
	 * 11 - Edgar	[10]
	 * 9  - Carlos	[11]
	 */
	static {
		VENDEDOR.put((long) ID_WILFRIDO[0], (long) ID_WILFRIDO[1]);
		VENDEDOR.put((long) ID_MIRIAN[0], (long) ID_MIRIAN[1]);
		VENDEDOR.put((long) ID_VICTOR[0], (long) ID_VICTOR[1]);
		VENDEDOR.put((long) ID_EDGAR[0], (long) ID_EDGAR[1]);
		VENDEDOR.put((long) ID_CARLOS[0], (long) ID_CARLOS[1]);
	}
	
	/**
	 * pobla las facturas del sistema anteriror (MRA)
	 */
	public void poblarFacturas() throws Exception {
		CSV csv = new CSV(cab, det, src);
		csv.start();
		
		while (csv.hashNext()) {
			
			String numero = csv.getDetalleString("NUMERO");
			long idvendedor = Long.parseLong(csv.getDetalleString("IDVENDEDOR"));
			
			System.out.println(numero + " - " + idvendedor);
			FacturaVentaMRA fac = new FacturaVentaMRA();
			fac.setNumero(numero);
			fac.setIdVendedor(VENDEDOR.get(idvendedor) == null? 0 : VENDEDOR.get(idvendedor));
			rr.saveObject(fac, "sys");
			
			CtaCteEmpresaMovimiento ctacte = rr.getCtaCteMovimientoByNumero(numero);
			if (ctacte != null) {
				ctacte.setIdVendedor(fac.getIdVendedor());
				rr.saveObject(ctacte, "sys");
				System.out.println("CtaCte actualizada..");
			}			
		}
	}
	
	/**
	 * setea los vendedores..
	 */
	public void setVendedores() throws Exception {
		String query = "select v from Venta v where v.tipoMovimiento.sigla = '"
				+ Configuracion.SIGLA_TM_FAC_VENTA_CREDITO + "'";
		@SuppressWarnings("unchecked")
		List<Venta> ventas = rr.hql(query);
		for (Venta venta : ventas) {
			CtaCteEmpresaMovimiento ctacte = rr
					.getCtaCteMovimientoByIdMovimiento(venta.getId(),
							Configuracion.SIGLA_TM_FAC_VENTA_CREDITO);
			if (ctacte != null && ctacte.getIdVendedor() == 0) {
				ctacte.setIdVendedor(venta.getVendedor().getId());
				rr.saveObject(ctacte, "sys");
				System.out.println("CtaCte actualizada..");
			}
		}
		System.out.println("Ejecucion terminada: " + Utiles.getDateToString(new Date(), Utiles.DD_MM_YYYY_HH_MM_SS));
	}
	
	public static void main(String[] args) {
		try {
			MigracionFacturasVenta test = new MigracionFacturasVenta();
			//test.poblarFacturas();
			test.setVendedores();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
