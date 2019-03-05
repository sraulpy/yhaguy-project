package com.yhaguy.gestion.reportes;

import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.QueryParam;

import com.coreweb.Config;
import com.coreweb.control.Control;
import com.coreweb.dto.Assembler;
import com.coreweb.login.LoginUsuario;
import com.coreweb.login.LoginUsuarioDTO;
import com.yhaguy.util.Utiles;

public class ReportesViewModel_ extends Control {

	public ReportesViewModel_() {
		super(null);
	}

	public ReportesViewModel_(Assembler ass) {
		super(ass);
	}

	@Init(superclass = true)
	public void init(@QueryParam("usuario") String usuario, @QueryParam("clave") String clave) throws Exception {		
		String clave_ = Utiles.Desencriptar(clave);		
		LoginUsuario login = new LoginUsuario();
		LoginUsuarioDTO uDto = login.log(usuario, clave_);
	
		this.setUs(uDto);
		this.setAliasFormularioCorriente(Config.ALIAS_HABILITADO_SI_O_SI);
	}	
}
