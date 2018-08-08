package com.yhaguy.process.tareasprogramadas;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.util.AutoNumeroControl;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.domain.CajaPlanillaResumen;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Tarea_Programada;
import com.yhaguy.util.Utiles;

public class TareaCerrarCajas {

	public static void main(String[] args) {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			String desde = Utiles.getDateToString(new Date(), Utiles.DD_MM_YYYY) + " 00:00:00";
			String hasta = Utiles.getDateToString(new Date(), Utiles.DD_MM_YYYY) + " 23:59:00";
			List<CajaPeriodo> cajas = rr.getCajaPlanillas(Utiles.getFecha(desde), Utiles.getFecha(hasta));
			List<CajaPeriodo> cajas_ = new ArrayList<CajaPeriodo>();
			for (CajaPeriodo caja : cajas) {
				if (caja.isAbierto() && (caja.isCajaVentas() || caja.isCajaCobros() || caja.isCajaCobrosMobile())) {
					caja.setDbEstado('R');
					caja.setCierre(new Date());
					caja.setEstado(rr.getTipoPorSigla("CAJ-PER-CER"));
					rr.saveObject(caja, "sys");
					cajas_.add(caja);
					
					Tarea_Programada tarea = new Tarea_Programada();
					tarea.setFecha(new Date());
					tarea.setDescripcion("CIERRE DE CAJA: " + caja.getNumero());
					rr.saveObject(tarea, "sys");
				}
			}
			if (cajas_.size() > 0) {
				CajaPlanillaResumen resumen = new CajaPlanillaResumen();
				resumen.setNumero(AutoNumeroControl.getAutoNumero("RESUMEN-", 7));
				resumen.setFecha(new Date());
				resumen.setSobranteFaltante(0);
				resumen.setEfectivoNoDepositado(0);
				resumen.setChequeNoDepositado(0);
				resumen.setObs_cheque_no_depositado("");
				resumen.setObs_efectivo_no_depositado("");
				resumen.getPlanillas().addAll(cajas_);
				String nroPlanillas = "";
				for (CajaPeriodo planilla : cajas_) {
					nroPlanillas += planilla.getNumero() + " - ";
				}
				resumen.setNumeroPlanillas(nroPlanillas);
				rr.saveObject(resumen, "sys");
			}
			System.out.println("CIERRE DE CAJAS REALIZADO..");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
