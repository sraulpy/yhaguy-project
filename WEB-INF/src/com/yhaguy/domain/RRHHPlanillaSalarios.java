package com.yhaguy.domain;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Domain;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class RRHHPlanillaSalarios extends Domain {
	
	public static final String SALARIOS = "SALARIOS";
	public static final String COMISION = "COMISION";
	public static final String ANTICIPO = "ANTICIPO";
	public static final String BONIFICACION = "BONIFICACION FAMILIAR";
	public static final String OTROS_HABERES = "OTROS HABERES";
	public static final String PRESTAMOS = "PRESTAMOS";
	public static final String ADELANTOS = "ANTICIPO COMISIÓN";
	public static final String OTROS_DESCUENTOS = "OTROS DESCUENTOS";
	public static final String CORPORATIVO = "CORPORATIVO";
	public static final String UNIFORME = "UNIFORME";
	public static final String REPUESTOS = "REPUESTOS";
	public static final String SEGURO = "SEGURO";
	public static final String EMBARGO = "EMBARGO";
	public static final String IPS = "IPS";
	public static final String HORAS_EXTRAS = "HORAS EXTRAS";
	public static final String RESPONSABILIDAD = "BONIFICACION POR RESPONSABILIDAD";
	public static final String VACACIONES = "VACACIONES";
	public static final String SEGURO_VEHICULAR = "SEGURO VEHICULO";
	public static final String AUSENCIA = "AUSENCIAS";
	public static final String AGUINALDO = "AGUINALDO";
	public static final String ANTICIPO_AGUINALDO = "ANTICIPO AGUINALDO";
	
	public static final String TIPO_COMISIONES = "COMISIONES";
	public static final String TIPO_SALARIOS = "SALARIOS";
	public static final String TIPO_PREMIOS = "PREMIOS";
	public static final String TIPO_AGUINALDOS = "AGUINALDOS";
	
	private String mes;
	private String anho;
	private String funcionario;
	private String cedula;
	private String cargo;
	private String tipo;
	
	private double diasTrabajados;
	private double cantidadHorasExtras;
	private double cantidadHorasExtrasNoc;
	
	private double salarios;
	private double comision;
	private double anticipo;
	private double bonificacion;
	private double otrosHaberes;
	private double totalHaberes;
	private double prestamos;
	private double adelantos;
	private double otrosDescuentos;
	private double corporativo;
	private double uniforme;
	private double repuestos;
	private double seguro;
	private double embargo;
	private double horasExtras;
	private double responsabilidad;
	private double vacaciones;
	private double seguroVehicular;
	private double ausencia;
	private double aguinaldo;
	private double anticipoAguinaldo;
	private double anticipoAguinaldo2;
	private double anticipoAguinaldo3;
	private boolean cerrado;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}
	
	@DependsOn({ "salarios", "responsabilidad" })
	public double getJornalDiario() {
		return (this.salarios + this.responsabilidad) / 30;
	}
	
	@DependsOn({ "salarios", "responsabilidad" })
	public double getJornalHora() {
		return this.getJornalDiario() / 8;
	}
	
	@DependsOn({ "salarios", "responsabilidad", "diasTrabajados" })
	public double getSalarioFinal() {
		return this.getJornalDiario() * this.diasTrabajados;
	}
		
	@DependsOn({ "salarios", "responsabilidad", "cantidadHorasExtras" })
	public double getExtrasDiurnas() {
		return this.cantidadHorasExtras * (this.getJornalHora() * 1.50);
	}
	
	@DependsOn({ "salarios", "responsabilidad", "cantidadHorasExtrasNoc" })
	public double getExtrasNocturnas() {
		return this.cantidadHorasExtrasNoc * (this.getJornalHora() * 2);
	}
	
	@DependsOn({ "salarios", "bonificacion", "otrosHaberes", "horasExtras", "responsabilidad", "comision",
			"vacaciones", "aguinaldo", "cantidadHorasExtrasNoc", "cantidadHorasExtras", "diasTrabajados" })
	public double getTotalHaberes_() {
		return this.getSalarioFinal() + this.bonificacion + this.otrosHaberes + this.getExtrasDiurnas() + this.getExtrasNocturnas()
				+ this.comision + this.vacaciones + this.aguinaldo;
	}
	
	@DependsOn({ "salarios", "comision", "anticipo", "bonificacion", "otrosHaberes", "otrosDescuentos", "corporativo",
			"uniforme", "repuestos", "seguro", "embargo", "ips", "prestamos", "adelantos", "horasExtras",
			"responsabilidad", "vacaciones", "seguroVehicular", "ausencia", "aguinaldo", "anticipoAguinaldo",
			"diasTrabajados", "cantidadHorasExtras", "cantidadHorasExtrasNoc", "anticipoAguinaldo2", "anticipoAguinaldo3" })
	public double getTotalACobrar() {
		return this.getSalarioFinal() + this.comision + this.anticipo + this.bonificacion + this.otrosHaberes
				+ this.otrosDescuentos + this.corporativo + this.uniforme + this.repuestos + this.seguro + this.embargo
				+ this.getIps() + this.prestamos + this.adelantos + this.getExtrasDiurnas() + this.getExtrasNocturnas() + this.vacaciones
				+ this.seguroVehicular + this.ausencia + this.aguinaldo + this.anticipoAguinaldo + this.anticipoAguinaldo2 + this.anticipoAguinaldo3;
	}
	
	@DependsOn({ "seguroVehicular", "prestamos", "anticipo", "otrosDescuentos", "corporativo", "uniforme", "repuestos",
			"seguro", "embargo", "ausencia", "anticipoAguinaldo", "ips", "salarios", "responsabilidad", "diasTrabajados", "adelantos" })
	public double getTotalADescontar() {
		return this.seguroVehicular + this.prestamos + this.anticipo + this.otrosDescuentos + this.corporativo
				+ this.uniforme + this.repuestos + this.seguro + this.embargo + this.ausencia + this.anticipoAguinaldo
				+ this.anticipoAguinaldo2 + this.anticipoAguinaldo3 + this.adelantos + this.getIps();
	}
	
	@DependsOn({ "salarios", "bonificacion", "otrosHaberes", "horasExtras", "responsabilidad", "adelantos", "comision",
		"vacaciones", "aguinaldo", "anticipoAguinaldo2", "anticipoAguinaldo3" })
	public double getIps() {
		if (this.tipo.equals(TIPO_PREMIOS)) {
			return 0.0;
		}
		return ((this.getTotalHaberes_() - (this.bonificacion + this.aguinaldo)) * 0.09) * -1 ;
	}
	
	/**
	 * @return el total a cobrar formateado..
	 */
	public String getTotalACobrar_() {
		return Utiles.getNumberFormat(this.getTotalACobrar());
	}
	
	/**
	 * @return el total a cobrar formateado..
	 */
	public String getTotalACobrarLetras() {
		return Utiles.numeroAletras(Utiles.getRedondeo(this.getTotalACobrar()));
	}

	public String getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(String funcionario) {
		this.funcionario = funcionario;
	}

	public double getSalarios() {
		return salarios;
	}

	public void setSalarios(double salarios) {
		this.salarios = salarios;
	}

	public double getComision() {
		return comision;
	}

	public void setComision(double comision) {
		this.comision = comision;
	}

	public double getAnticipo() {
		return anticipo;
	}

	public void setAnticipo(double anticipo) {
		this.anticipo = anticipo;
	}

	public double getBonificacion() {
		return bonificacion;
	}

	public void setBonificacion(double bonificacion) {
		this.bonificacion = bonificacion;
	}

	public double getOtrosHaberes() {
		return otrosHaberes;
	}

	public void setOtrosHaberes(double otrosHaberes) {
		this.otrosHaberes = otrosHaberes;
	}

	public double getTotalHaberes() {
		return totalHaberes;
	}

	public void setTotalHaberes(double totalHaberes) {
		this.totalHaberes = totalHaberes;
	}

	public double getPrestamos() {
		return prestamos;
	}

	public void setPrestamos(double prestamos) {
		this.prestamos = prestamos;
	}

	public double getAdelantos() {
		return adelantos;
	}

	public void setAdelantos(double adelantos) {
		this.adelantos = adelantos;
	}

	public double getOtrosDescuentos() {
		return otrosDescuentos;
	}

	public void setOtrosDescuentos(double otrosDescuentos) {
		this.otrosDescuentos = otrosDescuentos;
	}

	public double getCorporativo() {
		return corporativo;
	}

	public void setCorporativo(double corporativo) {
		this.corporativo = corporativo;
	}

	public double getUniforme() {
		return uniforme;
	}

	public void setUniforme(double uniforme) {
		this.uniforme = uniforme;
	}

	public double getRepuestos() {
		return repuestos;
	}

	public void setRepuestos(double repuestos) {
		this.repuestos = repuestos;
	}

	public double getSeguro() {
		return seguro;
	}

	public void setSeguro(double seguro) {
		this.seguro = seguro;
	}

	public double getEmbargo() {
		return embargo;
	}

	public void setEmbargo(double embargo) {
		this.embargo = embargo;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public String getAnho() {
		return anho;
	}

	public void setAnho(String anho) {
		this.anho = anho;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public double getHorasExtras() {
		return horasExtras;
	}

	public void setHorasExtras(double horasExtras) {
		this.horasExtras = horasExtras;
	}

	public double getResponsabilidad() {
		return responsabilidad;
	}

	public void setResponsabilidad(double responsabilidad) {
		this.responsabilidad = responsabilidad;
	}

	public double getVacaciones() {
		return vacaciones;
	}

	public void setVacaciones(double vacaciones) {
		this.vacaciones = vacaciones;
	}

	public double getSeguroVehicular() {
		return seguroVehicular;
	}

	public void setSeguroVehicular(double seguroVehicular) {
		this.seguroVehicular = seguroVehicular;
	}

	public double getAusencia() {
		return ausencia;
	}

	public void setAusencia(double ausencia) {
		this.ausencia = ausencia;
	}

	public double getDiasTrabajados() {
		return diasTrabajados;
	}

	public void setDiasTrabajados(double diasTrabajados) {
		this.diasTrabajados = diasTrabajados;
	}

	public double getCantidadHorasExtras() {
		return cantidadHorasExtras;
	}

	public void setCantidadHorasExtras(double cantidadHorasExtras) {
		this.cantidadHorasExtras = cantidadHorasExtras;
	}

	public double getAguinaldo() {
		return aguinaldo;
	}

	public void setAguinaldo(double aguinaldo) {
		this.aguinaldo = aguinaldo;
	}

	public double getAnticipoAguinaldo() {
		return anticipoAguinaldo;
	}

	public void setAnticipoAguinaldo(double anticipoAguinaldo) {
		this.anticipoAguinaldo = anticipoAguinaldo;
	}

	public double getCantidadHorasExtrasNoc() {
		return cantidadHorasExtrasNoc;
	}

	public void setCantidadHorasExtrasNoc(double cantidadHorasExtrasNoc) {
		this.cantidadHorasExtrasNoc = cantidadHorasExtrasNoc;
	}

	public boolean isCerrado() {
		return cerrado;
	}

	public void setCerrado(boolean cerrado) {
		this.cerrado = cerrado;
	}

	public double getAnticipoAguinaldo2() {
		return anticipoAguinaldo2;
	}

	public void setAnticipoAguinaldo2(double anticipoAguinaldo2) {
		this.anticipoAguinaldo2 = anticipoAguinaldo2;
	}

	public double getAnticipoAguinaldo3() {
		return anticipoAguinaldo3;
	}

	public void setAnticipoAguinaldo3(double anticipoAguinaldo3) {
		this.anticipoAguinaldo3 = anticipoAguinaldo3;
	}
}
