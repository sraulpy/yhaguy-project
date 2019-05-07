	package com.yhaguy.inicio;

import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.Config;
import com.coreweb.SistemaPropiedad;
import com.coreweb.control.Control;
import com.coreweb.login.LoginUsuarioDTO;
import com.yhaguy.AssemblerUtil;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.BancoCheque;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.articulos.buscador.BuscadorArticulos;
import com.yhaguy.gestion.empresa.ctacte.visor.VisorCtaCte;
import com.yhaguy.gestion.venta.misventas.MisVentas;
import com.yhaguy.gestion.venta.misventas.VentasPerdidas;
import com.yhaguy.util.Utiles;

public class Inicio {


	@SuppressWarnings({ "static-access" })
	public synchronized static void init() {
		// Para setear cualquier cosa antes de empezar
		SistemaPropiedad sisProp = new SistemaPropiedad();
		Configuracion.empresa = sisProp.getNombreEmpresa();

		// ver si tiene un UtilDTO seteado
		if (Control.existDtoUtil() == false) {
			
			Config.TEMA = Config.TIPO_TEMA_NORMAL;
			
			System.out.println("==== INICIO de la Aplicacion =========");
			
			Control.setEmpresa(Configuracion.empresa);
			Control.setInicialDtoUtil(new AssemblerUtil().getDTOUtil());
		}
	}

	public void afterLogin() throws Exception {
		// asi se recupera la session
		Session s = Sessions.getCurrent();
		
		LoginUsuarioDTO uDto = (LoginUsuarioDTO) s.getAttribute(Config.USUARIO);
		String login = uDto.getLogin();

		AssemblerAcceso as = new AssemblerAcceso();
		AccesoDTO aDto = (AccesoDTO) as.obtenerAccesoDTO(login);
		s.setAttribute(Configuracion.ACCESO, aDto);
		
		// guarda en la session el controlador del buscador de articulos..
		BuscadorArticulos ctr = new BuscadorArticulos();
		s.setAttribute(Config.CONTROLADOR_BUSCADOR_PRODUCTOS, ctr);
		
		// guarda en la session el controlador de la vista mis ventas..
		MisVentas ctrvtas = new MisVentas();
		s.setAttribute(Config.CONTROLADOR_MIS_VENTAS, ctrvtas);
		
		// guarda en la session el controlador de ventas perdidas..
		VentasPerdidas ctrvtasPerdidas = new VentasPerdidas();
		s.setAttribute(Config.CONTROLADOR_VENTAS_PERDIDAS, ctrvtasPerdidas);
		
		// guarda en la session el controlador del buscador de articulos..
		VisorCtaCte ctrVisorCtaCte = new VisorCtaCte();
		s.setAttribute(Config.CONTROLADOR_VISOR_CTACTE, ctrVisorCtaCte);
		
		// notifica la meta del vendedor y venta del mes..
		RegisterDomain rr = RegisterDomain.getInstance();
		Funcionario func = rr.getFuncionarioById(aDto.getFuncionario().getId());
		if (func.isVendedor()) {
			double meta = func.getMetaActual();
			double venta = func.getTotalVentasActual();
			double dif = meta - venta;
			String tipo = Clients.NOTIFICATION_TYPE_INFO;
			if (dif > 0) {
				double porc = Utiles.obtenerPorcentajeDelValor(meta - venta, meta);
				if (porc >= 30) {
					tipo = Clients.NOTIFICATION_TYPE_ERROR;
				}
			}			
			Clients.showNotification("\n HOLA "
					+ func.getRazonSocial().toUpperCase()
					+ "\n "
					+ "\n TU META ES DE Gs: " + Utiles.getNumberFormat(meta)
					+ "\n LLEVAS VENDIDO Gs: " + Utiles.getNumberFormat(venta),
					tipo, null, "overlap_end", 0);
		}
		
		// notifica los cheques propios a vencer..
		if (func.isAdministrador()) {
			Date desde = Utiles.getFecha("01-01-" + Utiles.getDateToString(new Date(), "yyyy") + " 00:00:00");
			List<BancoCheque> cheques = rr.getCheques("", "", "", "", "", "", true, true, desde, new Date(), "");
			double totalHoy = 0;
			for (BancoCheque cheque : cheques) {
				String vto = Utiles.getDateToString(cheque.getFechaVencimiento(), Utiles.DD_MM_YYYY);
				String hoy = Utiles.getDateToString(new Date(), Utiles.DD_MM_YYYY);
				if (vto.equals(hoy)) {
					totalHoy += cheque.getMonto();
				}
			}
			
			if (totalHoy > 0) {
				Clients.showNotification("\n HOLA "
						+ func.getRazonSocial().toUpperCase()
						+ "\n "
						+ "\n HOY VENCEN CHEQUES POR \n Gs: " + Utiles.getNumberFormat(totalHoy),
						Clients.NOTIFICATION_TYPE_INFO, null, "overlap_after", 0);
			}
		}
	}
}
