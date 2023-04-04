package com.yhaguy.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Domain;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class RRHHLiquidacionSalario extends Domain {

	public static final String MOTIVO_RENUNCIA = "RENUNCIA";
	public static final String MOTIVO_DESPIDO = "DESPIDO";
	
	private Date fecha;
	private String cargo;
	private double importeGs;
	
	private Date fechaIngreso;
	private double salario;
	private double jornalDiario;
	private int diasTrabajados;
	private double aguinaldo;
	private int diasPreAviso;
	private int diasIndemnizacion;
	private int vacacionesProporcionales;
	private int vacacionesCausadas;
	private String motivo;
	
	private Funcionario funcionario;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}
	
	/**
	 * @return antiguedad..
	 */
	@DependsOn("fechaIngreso")
	public int getAntiguedad() {
		if (this.getFechaIngreso() == null) {
			return 0;
		}
		return Utiles.getNumeroMeses(this.fechaIngreso, this.fecha);
	}
	
	@DependsOn("fechaIngreso")
	public int getAntiguedadDias() {
		if (this.getFechaIngreso() == null) {
			return 0;
		}
		return Integer.parseInt(Utiles.getDateToString(new Date(), "dd"));
	}
	
	@DependsOn({ "jornalDiario", "diasTrabajados" })
	public double getHaberesDiasTrabajados() {
		return this.jornalDiario * this.diasTrabajados;
	}
	
	@DependsOn({ "jornalDiario", "diasPreAviso" })
	public double getHaberesPreAviso() {
		return this.jornalDiario * this.diasPreAviso;
	}
	
	@DependsOn({ "jornalDiario", "diasIndemnizacion" })
	public double getHaberesIndemnizacion() {
		return this.jornalDiario * this.diasIndemnizacion;
	}
	
	@DependsOn({ "jornalDiario", "vacacionesProporcionales" })
	public double getHaberesVacacionesProporcional() {
		return this.jornalDiario * this.vacacionesProporcionales;
	}
	
	@DependsOn({ "jornalDiario", "vacacionesCausadas" })
	public double getHaberesVacacionesCausadas() {
		return this.jornalDiario * this.vacacionesCausadas;
	}
	
	@DependsOn({ "jornalDiario", "diasTrabajados", "diasPreAviso", "diasIndemnizacion", "vacacionesProporcionales", "vacacionesCausadas" })
	public double getTotalHaberes() {
		return this.getHaberesDiasTrabajados() + this.getHaberesPreAviso()
				+ this.getHaberesIndemnizacion() + this.getHaberesVacacionesProporcional() + this.getHaberesVacacionesCausadas();
	}
	
	@DependsOn({ "jornalDiario", "diasTrabajados", "diasPreAviso", "diasIndemnizacion", "vacacionesProporcionales", "vacacionesCausadas" })
	public double getIps() {
		return this.getTotalHaberes() * 0.09;
	}
	
	@DependsOn({ "jornalDiario", "diasTrabajados", "diasPreAviso", "diasIndemnizacion", "vacacionesProporcionales", "vacacionesCausadas", "aguinaldo" })
	public double getTotalACobrar() {
		return this.getTotalHaberes() + this.getAguinaldo() - this.getIps();
	}
	
	/**
	 * @return los motivos de liquidacion..
	 */
	public static List<String> getMotivos() {
		List<String> out = new ArrayList<String>();
		out.add(MOTIVO_DESPIDO); out.add(MOTIVO_RENUNCIA);
		return out;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public Funcionario getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(Funcionario funcionario) {
		this.funcionario = funcionario;
	}

	public double getImporteGs() {
		return importeGs;
	}

	public void setImporteGs(double importeGs) {
		this.importeGs = importeGs;
	}

	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public double getSalario() {
		return salario;
	}

	public void setSalario(double salario) {
		this.salario = salario;
	}

	public double getJornalDiario() {
		return jornalDiario;
	}

	public void setJornalDiario(double jornalDiario) {
		this.jornalDiario = jornalDiario;
	}

	public int getDiasTrabajados() {
		return diasTrabajados;
	}

	public void setDiasTrabajados(int diasTrabajados) {
		this.diasTrabajados = diasTrabajados;
	}

	public double getAguinaldo() {
		return aguinaldo;
	}

	public void setAguinaldo(double aguinaldo) {
		this.aguinaldo = aguinaldo;
	}

	public int getDiasPreAviso() {
		return diasPreAviso;
	}

	public void setDiasPreAviso(int diasPreAviso) {
		this.diasPreAviso = diasPreAviso;
	}

	public int getDiasIndemnizacion() {
		return diasIndemnizacion;
	}

	public void setDiasIndemnizacion(int diasIndemnizacion) {
		this.diasIndemnizacion = diasIndemnizacion;
	}

	public int getVacacionesProporcionales() {
		return vacacionesProporcionales;
	}

	public void setVacacionesProporcionales(int vacacionesProporcionales) {
		this.vacacionesProporcionales = vacacionesProporcionales;
	}

	public int getVacacionesCausadas() {
		return vacacionesCausadas;
	}

	public void setVacacionesCausadas(int vacacionesCausadas) {
		this.vacacionesCausadas = vacacionesCausadas;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
}
