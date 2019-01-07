package com.yhaguy.util.migracion.central;

import java.util.HashMap;
import java.util.Map;

import com.coreweb.extras.csv.CSV;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloUbicacion;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.Utiles;

public class MigracionCentral {

	static final String DIR_MIGRACION = "./WEB-INF/docs/migracion/central/";
	
	static final String SUC_CENTRAL = "CENTRAL";
	static final String SUC_MRA = "M.R.A";
	static final String SUC_MCAL = "MCAL";
	static final String SUC_GAM = "GRAN ALMACEN";
	
	/**
	 * proveedores..
	 */
	public static void migrarProveedores() throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();
		String src = DIR_MIGRACION + "PROVEEDORES.csv";
		String src_ = DIR_MIGRACION + "BATERIAS_SALDOS.csv";

		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "IDCUENTA", CSV.STRING }, { "RAZONSOCIAL", CSV.STRING }, { "RUC", CSV.STRING },
				{ "TELEFONO", CSV.STRING }, { "DIRECCION", CSV.STRING } };		  	  

		String[][] det_ = { { "RAZONSOCIAL", CSV.STRING }, { "NRODOCUMENTO", CSV.STRING }, { "CONCEPTO", CSV.STRING },
				{ "EMISION", CSV.STRING }, { "VENCIMIENTO", CSV.STRING }, { "IMPORTE", CSV.STRING }, { "SALDO", CSV.STRING }, { "IDCUENTA", CSV.STRING } };

		Map<String, Object[]> map = new HashMap<String, Object[]>();
		
		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {
			String idcuenta = csv.getDetalleString("IDCUENTA");
			String rs = csv.getDetalleString("RAZONSOCIAL");
			String ruc = csv.getDetalleString("RUC");
			String direccion = csv.getDetalleString("DIRECCION");
			String telefono = csv.getDetalleString("TELEFONO");
			
			Object[] datos = { rs, ruc, direccion, telefono };
			map.put(idcuenta, datos);
		}
		
		CSV csv_ = new CSV(cab, det_, src_);
		csv_.start();
		while (csv_.hashNext()) {
			String razonsocial = csv_.getDetalleString("RAZONSOCIAL");
			String concepto = csv_.getDetalleString("CONCEPTO");
			String nrodocumento = csv_.getDetalleString("NRODOCUMENTO");
			String emision = csv_.getDetalleString("EMISION");
			String vencimiento = csv_.getDetalleString("VENCIMIENTO");
			String importe = csv_.getDetalleString("IMPORTE");
			String saldo = csv_.getDetalleString("SALDO");			
			String idcuenta = csv_.getDetalleString("IDCUENTA");
			Object[] datos = map.get(idcuenta);
			
			if (datos != null) {
				Empresa emp = rr.getEmpresa(((String) datos[0]).replace("'", ""));
				if (emp == null) {
					emp = new Empresa();
					emp.setRazonSocial((String) datos[0]);
					emp.setNombre((String) datos[0]);
					emp.setRuc((String) datos[1]);
					emp.setDireccion_((String) datos[2]);
					emp.setTelefono_((String) datos[3]);
					rr.saveObject(emp, "sys");

					Proveedor pr = new Proveedor();
					pr.setEmpresa(emp);
					rr.saveObject(pr, "sys");
				} else {
					Proveedor prov = rr.getProveedorByEmpresa(emp.getId());
					if (prov == null) {
						prov = new Proveedor();
						prov.setEmpresa(emp);
						rr.saveObject(prov, "sys");
					}
				}
				
				CtaCteEmpresaMovimiento movim = new CtaCteEmpresaMovimiento();
				movim.setAnulado(false);
				movim.setCerrado(false);
				movim.setFechaEmision(Utiles.getFecha(emision, "dd/MM/yyyy"));
				movim.setFechaVencimiento(Utiles.getFecha(vencimiento, "dd/MM/yyyy"));
				movim.setIdEmpresa(emp.getId());
				movim.setIdMovimientoOriginal(0);
				movim.setIdVendedor(0);
				movim.setImporteOriginal(Double.parseDouble(importe.replace(",", ".")));
				movim.setMoneda(concepto.equals("Proveedores Ext.") ? rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_DOLAR) : rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
				movim.setNroComprobante(nrodocumento);
				movim.setNumeroImportacion("");
				movim.setSaldo(Double.parseDouble(saldo.replace(",", ".")));
				movim.setSucursal(rr.getSucursalAppById(2));
				movim.setTipoCambio(1);
				movim.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR));
				movim.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_COMPRA_CREDITO));
				
				rr.saveObject(movim, "sys");
				
				System.out.println(movim.getNroComprobante());
			
			} else {
				System.err.println(razonsocial);
			}
		}
	}
	
	/**
	 * ubicaciones..
	 */
	public static void migrarUbicaciones() throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();
		String src = DIR_MIGRACION + "UBICACIONES.csv";

		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CODIGO", CSV.STRING }, { "UBICACION", CSV.STRING }, { "DEPOSITO", CSV.STRING } };		
		
		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {
			String codigo = csv.getDetalleString("CODIGO");
			String ubicacion = csv.getDetalleString("UBICACION");
			String deposito = csv.getDetalleString("DEPOSITO");
			
			Articulo art = rr.getArticulo(codigo);
			if (art != null) {
				ArticuloUbicacion ubic = new ArticuloUbicacion();
				ubic.setEstante(deposito.equals("1") ? "MINORISTA" : "MAYORISTA");
				ubic.setFila(ubicacion);
				art.getUbicaciones().add(ubic);
				rr.saveObject(art, art.getUsuarioMod());
				System.out.println(art.getCodigoInterno() + " - " + ubic.getFila() + " - " + ubic.getEstante());
			}
		}
	}
	
	/**
	 * ubicaciones..
	 */
	public static void migrarTelecobradores() throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();
		String src = DIR_MIGRACION + "CLIENTES_TELECOBRADORES.csv";
		
		Map<String, Funcionario> telecob = new HashMap<String, Funcionario>();
		Funcionario vivi = rr.getFuncionario((long) 14);
		Funcionario claudia = rr.getFuncionario((long) 13);
		Funcionario laura = rr.getFuncionario((long) 44);
		telecob.put("0", null);
		telecob.put("1", laura);
		telecob.put("2", claudia);
		telecob.put("3", vivi);

		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "RAZONSOCIAL", CSV.STRING }, { "IDCOBRADOR", CSV.STRING }, { "COBRADOR", CSV.STRING } };		
		
		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {
			String razonSocial = csv.getDetalleString("RAZONSOCIAL");
			String idcobrador = csv.getDetalleString("IDCOBRADOR");
			//String cobrador = csv.getDetalleString("COBRADOR");			
			
			Cliente cli = rr.getClienteByRazonSocial(razonSocial);
			if (cli != null) {
				Funcionario cob = telecob.get(idcobrador);
				cli.setCobrador(cob);
				rr.saveObject(cli, cli.getUsuarioMod());
				System.out.println(cob == null? "SIN" : cob.getRazonSocial() + " " + cli.getRazonSocial());
			}
		}
	}
	
	
	public static void main(String[] args) {
		try {
			
		/**	MigracionCentral.migrarArticuloFamilias();
			MigracionCentral.migrarArticuloGrupo();
			MigracionCentral.migrarArticuloMarca();
			MigracionCentral.migrarArticuloAplicacion();
			MigracionCentral.migrarArticuloModelo();
			MigracionCentral.migrarArticuloLinea();
			MigracionCentral.migrarArticuloSubLinea();
			MigracionCentral.migrarArticuloSubGrupo();
			MigracionCentral.migrarArticuloAPI();
			MigracionCentral.migrarArticuloProcedencia();
			MigracionCentral.migrarArticulos("ARTICULO_REPUESTOS");
			MigracionCentral.migrarArticulos("ARTICULO_FILTROS");
			MigracionCentral.migrarArticulos("ARTICULO_LUBRICANTES");
			MigracionCentral.migrarArticulos("ARTICULO_NEUMATICOS");
			MigracionCentral.migrarArticulos("ARTICULO_BATERIAS");
			MigracionCentral.migrarArticulos("ARTICULO_SERVICIOS");
			MigracionCentral.migrarArticulos("ARTICULO_RETAIL");
			MigracionCentral.migrarArticulos("ARTICULO_MARKETING"); 
			MigracionCentral.machearArticulosSaldos("1_STOCK_MINORISTA", 1);
			MigracionCentral.machearArticulosSaldos("2_STOCK_TEMPORAL_CENTRAL", 2);
			MigracionCentral.machearArticulosSaldos("3_STOCK_RECLAMOS_CENTRAL", 3);
			MigracionCentral.machearArticulosSaldos("4_STOCK_REPOSICION_CENTRAL", 4);
			MigracionCentral.machearArticulosSaldos("5_STOCK_MCAL", 5); 
			MigracionCentral.machearArticulosSaldos("5_STOCK_MCAL_", 5);
			MigracionCentral.machearArticulosSaldos("6_STOCK_MCAL_TEMPORAL", 6);
			MigracionCentral.machearArticulosSaldos("7_STOCK_MAYORISTA", 7); 
			MigracionCentral.machearArticulosSaldos("8_STOCK_MAYORISTA_TEMPORAL", 8); **/
			
		//	MigracionCentral.migrarVendedores();
		//	MigracionCentral.migrarRubrosClientes();
		//	MigracionCentral.migrarClientes();
		//	MigracionCentral.machearClientesVendedores();
		//	MigracionCentral.machearClientesSaldos();
		//	MigracionCentral.migrarProveedores();
		//	MigracionCentral.migrarFuncionarios();
		//	MigracionCentral.migrarChequesRechazados();
			
		//	MigracionCentral.migrarUbicaciones();
			MigracionCentral.migrarTelecobradores();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
