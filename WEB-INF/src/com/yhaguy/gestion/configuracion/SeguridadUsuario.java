package com.yhaguy.gestion.configuracion;

import java.util.Date;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.Usuario;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.util.Utiles;

public class SeguridadUsuario extends SimpleViewModel {
	
	static final String ZUL_SEGURIDAD_USUARIO = "/yhaguy/gestion/configuracion/infoUsuario.zul";
	
	private Usuario user;
	private Funcionario func;
	private Window win;
	
	private String claveNueva;
	private String claveNuevaVerificar;
	
	@Init(superclass = true)
	public void init(@BindingParam("parent") Component parent) {
	}
	
	@Command
	public void verificarUsuario() {
		try {
			long idFuncionario = this.getAcceso().getFuncionario().getId();
			long idUsuario = this.getAcceso().getUsuario().getId();
			RegisterDomain rr = RegisterDomain.getInstance();
			this.user = rr.getUsuario(idUsuario);
			this.func = rr.getFuncionario_(idFuncionario);		
			
			if (this.func.getUltimoCambioPassword() == null
					|| Utiles.diasEntreFechas(this.func.getUltimoCambioPassword(), new Date()) > 90) {
				this.win = (Window) Executions.createComponents(ZUL_SEGURIDAD_USUARIO, this.mainComponent, null);
				this.win.doModal();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
	
	@Command
	public void cambiarPassword() {
		if (!this.validar()) {
			return;
		}
		try {
			Misc m = new Misc();
			String claveEncriptada = m.encriptar(this.claveNueva, false);
			RegisterDomain rr = RegisterDomain.getInstance();
			this.user.setClave(claveEncriptada);
			this.func.setUltimoCambioPassword(new Date());
			rr.saveObject(this.user, this.user.getLogin());
			rr.saveObject(this.func, this.user.getLogin());
			Clients.showNotification("CREDENCIALES ACTUALIZADAS", this.mainComponent);
			this.win.detach();
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * @return validador.
	 */
	private boolean validar() {
		boolean out = true;

		if (!this.claveNueva.equals(this.claveNuevaVerificar)) {
			Clients.showNotification("NO COINCIDEN LAS CLAVES.", Clients.NOTIFICATION_TYPE_ERROR, this.mainComponent, null, 0);
			return false;
		}

		String regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{5,20}$";
		if (!Utiles.isValidPassword(this.claveNueva, regex)) {
			Clients.showNotification("LA CLAVE NO CUMPLE CON LOS REQUISITOS, FAVOR VERIFIQUE.",
					Clients.NOTIFICATION_TYPE_ERROR, this.mainComponent, null, 0);
			return false;
		}
		return out;
	}
	
	public AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		AccesoDTO out = (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
		return out;
	}

	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}

	public String getClaveNueva() {
		return claveNueva;
	}

	public void setClaveNueva(String claveNueva) {
		this.claveNueva = claveNueva;
	}

	public String getClaveNuevaVerificar() {
		return claveNuevaVerificar;
	}

	public void setClaveNuevaVerificar(String claveNuevaVerificar) {
		this.claveNuevaVerificar = claveNuevaVerificar;
	}
}
