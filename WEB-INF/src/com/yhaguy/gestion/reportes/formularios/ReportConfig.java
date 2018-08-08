package com.yhaguy.gestion.reportes.formularios;

import java.util.HashMap;
import java.util.Map;

import com.yhaguy.gestion.caja.periodo.NotaCreditoDataSource;
import com.yhaguy.gestion.caja.periodo.VentaDataSource;

import net.sf.jasperreports.engine.JRDataSource;

public class ReportConfig {
	
	private String source;
    private Map<String, Object> parameters;
    private JRDataSource dataSource;

	public ReportConfig() {
		parameters = new HashMap<String, Object>();
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public JRDataSource getDataSource() {
		return dataSource;
	}
	
	public VentaDataSource getVentaDataSource1() throws InstantiationException, IllegalAccessException {
		return ((VentaDataSource) this.dataSource).clone();
	}
	
	public VentaDataSource getVentaDataSource2() throws InstantiationException, IllegalAccessException {
		return ((VentaDataSource) this.dataSource).clone();
	}
	
	public NotaCreditoDataSource getNotaCreditoDataSource1() throws InstantiationException, IllegalAccessException {
		return ((NotaCreditoDataSource) this.dataSource).clone();
	}
	
	public NotaCreditoDataSource getNotaCreditoDataSource2() throws InstantiationException, IllegalAccessException {
		return ((NotaCreditoDataSource) this.dataSource).clone();
	}

	public void setDataSource(JRDataSource dataSource) {
		this.dataSource = dataSource;
	}      
}
