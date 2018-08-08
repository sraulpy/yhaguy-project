package com.yhaguy.util.migracion;

import java.util.Hashtable;

import com.coreweb.domain.Tipo;
import com.coreweb.extras.csv.CSV;
import com.coreweb.util.AutoNumeroControl;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.CuentaContable;
import com.yhaguy.domain.PlanDeCuenta;
import com.yhaguy.domain.RegisterDomain;

public class MigracionPlanDeCuentas {

	static RegisterDomain rr = RegisterDomain.getInstance();

	static String src = "./WEB-INF/docs/migracion/PlanCuentasFiscal.csv";
	static String srcCt = "./WEB-INF/docs/migracion/Cuentas.csv";

	static Hashtable<String, Tipo> tipos = new Hashtable<String, Tipo>();
	static Tipo activo = new Tipo();
	static Tipo pasivo = new Tipo();
//	static Tipo patrimonio = new Tipo();
	static Tipo ingreso = new Tipo();
	static Tipo egreso = new Tipo();

	static {
		try {

			activo = rr
					.getTipoPorSigla(Configuracion.SIGLA_TIPO_CTA_CONTABLE_ACTIVO);
			pasivo = rr
					.getTipoPorSigla(Configuracion.SIGLA_TIPO_CTA_CONTABLE_PASIVO);

			ingreso = rr
					.getTipoPorSigla(Configuracion.SIGLA_TIPO_CTA_CONTABLE_INGRESO);
			egreso = rr
					.getTipoPorSigla(Configuracion.SIGLA_TIPO_CTA_CONTABLE_EGRESO);

			tipos.put("1", activo);
			tipos.put("2", pasivo);

			tipos.put("3", ingreso);
			tipos.put("4", egreso);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Plan de Cuentas..
	 */

	String[][] cab = { { "Empresa", CSV.STRING } };
	String[][] det = { { "IDPLANCUENTA", CSV.STRING },
			{ "NOMBRE", CSV.STRING }, { "IDTIPOCUENTA", CSV.STRING },
			{ "IMPUTABLE", CSV.STRING }, { "IMPOSITIVO", CSV.STRING },
			{ "CCOSTO", CSV.STRING }, { "NIVEL", CSV.NUMERICO }, };

	public void poblarPlanDeCuentas() throws Exception {

		System.out
				.println("---------------------- Poblando Plan de Cuentas ----------------------");

		CSV csv = new CSV(cab, det, src);

		csv.start();
		while (csv.hashNext()) {

			String codigo = csv.getDetalleString("IDPLANCUENTA");
			String descripcion = csv.getDetalleString("NOMBRE");
			String tipoCta = csv.getDetalleString("IDTIPOCUENTA");

			String imputable = csv.getDetalleString("IMPUTABLE");
			String impositivo = csv.getDetalleString("IMPOSITIVO");
			String ccosto = csv.getDetalleString("CCOSTO");
			int nivel = ((Float)csv.getDetalle("NIVEL")).intValue();
			
			
			PlanDeCuenta pct = new PlanDeCuenta();
			pct.setCodigo(codigo);
			pct.setDescripcion(descripcion);
			pct.setTipoCuenta(tipos.get(tipoCta));
			pct.setImputable(imputable);
			pct.setImpositivo(impositivo);
			pct.setCcosto(ccosto);
			pct.setNivel(nivel);

			rr.saveObject(pct, "Migracion");
		}

		System.out
				.println("---------------------- Fin Plan de Cuentas ----------------------");
	}

	/**
	 * Cuentas Contables..
	 */

	String[][] cabCt = { { "Empresa", CSV.STRING } };
	String[][] detCt = { { "DESCRIPCION", CSV.STRING },
			{ "IDPLANCUENTA", CSV.STRING }, { "ALIAS", CSV.STRING } };

	public void poblarCuentasContables() throws Exception {

		System.out
				.println("---------------------- Poblando Cuentas Contables ----------------------");

		CSV csv = new CSV(cabCt, detCt, srcCt);

		csv.start();
		while (csv.hashNext()) {

			String descripcion = csv.getDetalleString("DESCRIPCION");
			String idPlanCuenta = csv.getDetalleString("IDPLANCUENTA");
			String alias = csv.getDetalleString("ALIAS");
			String key = Configuracion.NRO_CUENTA_CONTABLE;

			PlanDeCuenta pct = null;
			try {
				pct = rr.getPlanDeCuentaByCodigo(idPlanCuenta);
				
			} catch (Exception e) {
				if (pct == null){
					System.err.println("No encuentra el Plan de Cuenta ["+idPlanCuenta+"] ");
					pct = (PlanDeCuenta) rr.getObject(PlanDeCuenta.class.getName(), 1);
				}
			}

			CuentaContable ct = new CuentaContable();
			ct.setCodigo(key + "-" + AutoNumeroControl.getAutoNumero(key, 7));
			ct.setDescripcion(descripcion);
			ct.setPlanCuenta(pct);

			if (alias.compareTo("0") == 0) {
				ct.setAlias("CT-" + descripcion.substring(0, 2) + "-"
						+ ct.getCodigo().substring(3));
			} else {
				ct.setAlias(alias);
			}
			rr.saveObject(ct, "Migracion");
		}

		System.out
				.println("---------------------- Fin Cuentas Contables ----------------------");
	}

	public static void main(String[] args) throws Exception {
		MigracionPlanDeCuentas m = new MigracionPlanDeCuentas();
		m.poblarPlanDeCuentas();
		m.poblarCuentasContables();
	}
}
