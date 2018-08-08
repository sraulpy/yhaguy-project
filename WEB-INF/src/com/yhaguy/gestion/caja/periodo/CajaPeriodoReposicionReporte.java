package com.yhaguy.gestion.caja.periodo;

import java.util.ArrayList;
import java.util.List;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

import com.yhaguy.util.reporte.ReporteYhaguy;

public class CajaPeriodoReposicionReporte extends ReporteYhaguy {

	private String caja = "";
	private String planilla = "";
	private String apertura = "";
	private String responsable = "";
	private String verificado = "";
	private String sucursal = "";
	private String tipoEgreso = "";
	private String funcionario = "";
	private String moneda = "";
	private String tipoCambio = "";
	private String monto = "";
	private String observacion = "";

	@Override
	public void informacionReporte() {
		// titulo
		this.setTitulo("Reporte Caja Vale");
		this.setBody(this.getCabecera());

		// texto pie de pagina
		this.setFooter(this.textoAutorizado());
	}

	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCabecera() {

		VerticalListBuilder out = null;

		out = cmp.verticalList();

		HorizontalListBuilder f1 = cmp.horizontalList();
		f1.add(this.textoParValor("Caja ", this.caja));
		f1.add(this.textoParValor("Planilla ", this.planilla));
		
		
		HorizontalListBuilder f2 = cmp.horizontalList();
		f2.add(this.textoParValor("Sucursal ", this.sucursal));
		f2.add(this.textoParValor("Funcionario ", this.funcionario));
		
		HorizontalListBuilder f3 = cmp.horizontalList();
		f3.add(this.textoParValor("Moneda ", this.moneda));
		f3.add(this.textoParValor("Observacion", this.observacion));
			
		

		out.add(f1);
		out.add(f2);
		out.add(f3);
		

		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}

	/******************** GET/SET ********************/

	public String getCaja() {
		return caja;
	}

	public void setCaja(String caja) {
		this.caja = caja;
	}

	public String getPlanilla() {
		return planilla;
	}

	public void setPlanilla(String planilla) {
		this.planilla = planilla;
	}

	public String getApertura() {
		return apertura;
	}

	public void setApertura(String apertura) {
		this.apertura = apertura;
	}

	public String getResponsable() {
		return responsable;
	}

	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}

	public String getVerificado() {
		return verificado;
	}

	public void setVerificado(String verificado) {
		this.verificado = verificado;
	}

	public String getSucursal() {
		return sucursal;
	}

	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}

	public String getTipoEgreso() {
		return tipoEgreso;
	}

	public void setTipoEgreso(String tipoEgreso) {
		this.tipoEgreso = tipoEgreso;
	}

	public String getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(String funcionario) {
		this.funcionario = funcionario;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public String getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(String tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	public String getMonto() {
		return monto;
	}

	public void setMonto(String monto) {
		this.monto = monto;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public static void main(String[] args) throws Exception {

		List<Object[]> data = new ArrayList<>();
		CajaPeriodoReposicionReporte cpr = new CajaPeriodoReposicionReporte();
		cpr.setPlanilla("");
		cpr.setDatosReporte(data);
		cpr.ejecutar(true);

	}

}
