package com.yhaguy.gestion.reportes;

import java.util.Iterator;
import java.util.List;

import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.QueryParam;

import com.coreweb.Config;
import com.coreweb.control.Control;
import com.coreweb.domain.Formulario;
import com.coreweb.domain.Perfil;
import com.coreweb.domain.Permiso;
import com.coreweb.domain.Register;
import com.coreweb.domain.Usuario;
import com.coreweb.domain.UsuarioPropiedad;
import com.coreweb.dto.Assembler;
import com.coreweb.login.LoginUsuarioDTO;

public class ReportesViewModel_ extends Control {

	public ReportesViewModel_() {
		super(null);
	}

	public ReportesViewModel_(Assembler ass) {
		super(ass);
	}

	@Init(superclass = true)
	public void init(@QueryParam("usuario") String usuario, @QueryParam("clave") String clave) throws Exception {				
		System.out.println("usuario: " + usuario);
		LoginUsuarioDTO uDto = this.log(usuario);
	
		this.setUs(uDto);
		this.setAliasFormularioCorriente(Config.ALIAS_HABILITADO_SI_O_SI);
	}	
	
	public LoginUsuarioDTO log(String login) throws Exception {
		
		LoginUsuarioDTO lu = new LoginUsuarioDTO();
		Register rr = Register.getInstance();
		Usuario u = rr.getUsuario(login);
		
		if (u != null) {
			lu.setLogeado(true);
			lu.setNombre(u.getNombre());
			lu.setLogin(u.getLogin());
			lu.setId(u.getId());

			int cp = 0;
			String[] nombrePerfiles = new String[u.getPerfiles().size()];
			String[] nombrePerfilesDesc = new String[u.getPerfiles().size()];

			
			// recorre las propiedades 
			Iterator<UsuarioPropiedad> iteProp = u.getUsuarioPropiedades().iterator();
			while(iteProp.hasNext()){
				UsuarioPropiedad up = iteProp.next();
				lu.addPropiedad(up.getClave().getDescripcion(), up.getValor());
			}
			
			
			// recorre los perfiles
			Iterator<Perfil> iteP = u.getPerfiles().iterator();
			while (iteP.hasNext()) {

				Perfil p = iteP.next();
				nombrePerfiles[cp] = p.getNombre(); 
				nombrePerfilesDesc[cp] = p.getDescripcion() + " ["+p.getGrupo()+"]"; 
				lu.addGrupo(p.getGrupo());
				
				// recorre los permisos, considera que el formulario y la operacion esten habilitadas
				Iterator<Permiso> itePerm = p.getPermisos().iterator();
				while (itePerm.hasNext()) {
					Permiso per = itePerm.next();

					String formAlias = per.getOperacion().getFormulario().getAlias();
					String formLabel = per.getOperacion().getFormulario().getLabel();
					String  formUrl = per.getOperacion().getFormulario().getUrl();
					boolean formHab = per.getOperacion().getFormulario().isHabilitado();
					boolean opeHab = per.getOperacion().isHabilitado();					
					String opeAlias = per.getOperacion().getAlias();
					boolean perHab = per.isHabilitado();
					
					// un formulario estará deshabilitado, está deshabilitado para todos
					
					lu.addFormulario(formAlias, formLabel, formUrl, formHab);
					lu.addOperacion(formAlias, opeAlias, (formHab && opeHab && perHab));
				}
				cp++;
			}		
			lu.setPerfiles(nombrePerfiles);
			lu.setPerfilesDescripcion(nombrePerfilesDesc);
			
			
			// recorre los formularios para registrar todos los alias
			List<Formulario> lf = rr.getAllFormulario();
			for (int i = 0; i < lf.size(); i++) {
				Formulario f = lf.get(i);
				lu.addAllFormulario(f.getAlias(), f.getLabel());
			}
			
		}
		
		return lu;
	}
}
