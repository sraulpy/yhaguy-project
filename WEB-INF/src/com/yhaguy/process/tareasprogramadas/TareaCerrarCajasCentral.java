package com.yhaguy.process.tareasprogramadas;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.util.AutoNumeroControl;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.domain.CajaPlanillaResumen;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.domain.Tarea_Programada;
import com.yhaguy.util.Utiles;

public class TareaCerrarCajasCentral {

	public static void main(String[] args) {
		try {			
				Date fecha = new Date();
				RegisterDomain rr = RegisterDomain.getInstance();
				String desde = Utiles.getDateToString(fecha, Utiles.DD_MM_YYYY) + " 00:00:00";
				String hasta = Utiles.getDateToString(fecha, Utiles.DD_MM_YYYY) + " 23:59:00";
				List<CajaPeriodo> cajas = rr.getCajaPlanillas(Utiles.getFecha(desde), Utiles.getFecha(hasta));
				List<CajaPeriodo> cajas_ = new ArrayList<CajaPeriodo>();
				List<CajaPeriodo> cajas_mcal = new ArrayList<CajaPeriodo>();
				
				for (CajaPeriodo caja : cajas) {
					if (caja.getCaja().getSucursal().getId() == SucursalApp.ID_CENTRAL) {
						if (caja.isCajaVentas() || caja.isCajaCobros() || caja.isCajaCobrosMobile()) {
							caja.setDbEstado('R');
							caja.setCierre(fecha);
							caja.setEstado(rr.getTipoPorSigla("CAJ-PER-CER"));
							rr.saveObject(caja, "sys");
							cajas_.add(caja);
						}
					}				
				}	
				
				if (cajas_.size() > 0) {
					CajaPlanillaResumen resumen = new CajaPlanillaResumen();
					resumen.setNumero(AutoNumeroControl.getAutoNumero("RESUMEN-", 7));
					resumen.setFecha(fecha);
					resumen.setSobranteFaltante(0);
					resumen.setEfectivoNoDepositado(0);
					resumen.setChequeNoDepositado(0);
					resumen.setObs_cheque_no_depositado("");
					resumen.setObs_efectivo_no_depositado("");
					resumen.getPlanillas().addAll(cajas_);
					String nroPlanillas = "CENTRAL: ";
					for (CajaPeriodo planilla : cajas_) {
						nroPlanillas += planilla.getNumero() + " - ";
					}
					resumen.setNumeroPlanillas(nroPlanillas);
					rr.saveObject(resumen, "sys");
				}
				
				for (CajaPeriodo caja : cajas) {
					if (caja.getCaja().getSucursal().getId() == SucursalApp.ID_MCAL) {
						if (caja.isCajaVentas() || caja.isCajaCobros() || caja.isCajaCobrosMobile()) {
							caja.setDbEstado('R');
							caja.setCierre(fecha);
							caja.setEstado(rr.getTipoPorSigla("CAJ-PER-CER"));
							rr.saveObject(caja, "sys");
							cajas_mcal.add(caja);
						}
					}				
				}
				
				if (cajas_mcal.size() > 0) {
					CajaPlanillaResumen resumen = new CajaPlanillaResumen();
					resumen.setNumero(AutoNumeroControl.getAutoNumero("RESUMEN-", 7));
					resumen.setFecha(fecha);
					resumen.setSobranteFaltante(0);
					resumen.setEfectivoNoDepositado(0);
					resumen.setChequeNoDepositado(0);
					resumen.setObs_cheque_no_depositado("");
					resumen.setObs_efectivo_no_depositado("");
					resumen.getPlanillas().addAll(cajas_mcal);
					String nroPlanillas = "MCAL: ";
					for (CajaPeriodo planilla : cajas_mcal) {
						nroPlanillas += planilla.getNumero() + " - ";
					}
					resumen.setNumeroPlanillas(nroPlanillas);
					rr.saveObject(resumen, "sys");
				}
				
				Tarea_Programada tarea = new Tarea_Programada();
				tarea.setFecha(fecha);
				tarea.setDescripcion("RESUMEN DE CAJAS: " + AutoNumeroControl.getAutoNumero("RESUMEN-", 7, true));
				rr.saveObject(tarea, "sys");
							
				System.out.println("CIERRE DE CAJAS REALIZADO..");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
