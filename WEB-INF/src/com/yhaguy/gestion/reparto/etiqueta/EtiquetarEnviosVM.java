package com.yhaguy.gestion.reparto.etiqueta;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.Configuracion;

public class EtiquetarEnviosVM extends SimpleViewModel {
	
	static final String ZUL_IMPRESION_ETIQUETA = "/yhaguy/gestion/reparto/impresion_etiqueta.zul";
	
	private String cliente = "";
	private String destino = "";
	private int bultos = 1;
	private Date fecha = new Date();
	
	private Window win;

	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	public void imprimirEtiqueta() {
		for (int i = 1; i <= bultos; i++) {
			Map<String, String> args = new HashMap<>();
			args.put("id_win", "win_" + i);
			args.put("cliente", this.cliente);
			args.put("destino", this.destino);
			args.put("bulto", i + "/" + bultos);
			this.win = (Window) Executions.createComponents(ZUL_IMPRESION_ETIQUETA, this.mainComponent, args);
			this.win.doModal();
		}	
	}

	
	/**
	 * GETS / SETS
	 */
	
	/**
	 * @return la empresa..
	 */
	public String getEmpresa_() {
		return Configuracion.empresa.toUpperCase();
	}
	
	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public int getBultos() {
		return bultos;
	}

	public void setBultos(int bultos) {
		this.bultos = bultos;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}	
}
