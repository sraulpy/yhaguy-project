package com.yhaguy.domain;

import java.util.Date;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Domain;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class RRHHPermiso extends Domain {
	
	public static final String TIPO_PERMISO = "P";
	public static final String TIPO_VACACIONES = "V";

	private Date fecha;
	private String cargo;
	private String cedula;
	private String departamento;
	private String motivo;
	private String observacion;
	
	private Date salida;
	private Date regreso;
	private Date reincorporacion;
	private int dias;
	private boolean aprobado;
	
	private String tipo;
	
	private Funcionario funcionario;
	private Funcionario supervisor;
	private Funcionario reemplazante;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}
	
	@DependsOn("funcionario")
	public FuncionarioPeriodoVacaciones getPeriodo() {
		if (this.funcionario != null) {
			for (FuncionarioPeriodoVacaciones p : this.funcionario.getPeriodos()) {
				if (p.isVigente()) {
					return p;
				}
			}
		}		
		return null;
	}
	
	@DependsOn("funcionario")
	public String getPeriodo_() {
		FuncionarioPeriodoVacaciones p = this.getPeriodo();
		return p != null ? Utiles.getDateToString(p.getVigencia(), "dd-MM-yyyy") : "SIN PERIODO VIGENTE..";
	}
	
	@DependsOn("funcionario")
	public int getSaldo() {
		FuncionarioPeriodoVacaciones p = this.getPeriodo();
		return p != null ? p.getSaldo() : 0;
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

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public Date getSalida() {
		return salida;
	}

	public void setSalida(Date salida) {
		this.salida = salida;
	}

	public Date getRegreso() {
		return regreso;
	}

	public void setRegreso(Date regreso) {
		this.regreso = regreso;
	}

	public boolean isAprobado() {
		return aprobado;
	}

	public void setAprobado(boolean aprobado) {
		this.aprobado = aprobado;
	}

	public Funcionario getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(Funcionario supervisor) {
		this.supervisor = supervisor;
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

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public int getDias() {
		return dias;
	}

	public void setDias(int dias) {
		this.dias = dias;
	}

	public Funcionario getReemplazante() {
		return reemplazante;
	}

	public void setReemplazante(Funcionario reemplazante) {
		this.reemplazante = reemplazante;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Date getReincorporacion() {
		return reincorporacion;
	}

	public void setReincorporacion(Date reincorporacion) {
		this.reincorporacion = reincorporacion;
	}
}
