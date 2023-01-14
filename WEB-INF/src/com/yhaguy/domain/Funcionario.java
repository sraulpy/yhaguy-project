package com.yhaguy.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class Funcionario extends Domain {
	
	public static final String ESTADO_CIVIL_CASADO = "CASADO/A";
	public static final String ESTADO_CIVIL_SOLTERO = "SOLTERO/A";
	
	public static final String BACHILLER = "BACHILLER";
	public static final String LICENCIADO = "LICENCIADO";
	public static final String MAGISTER = "MAGISTER";
	public static final String DOCTORADO = "DOCTORADO";
	public static final String INGENIERO = "INGENIERO";
	public static final String SIN_ESTUDIOS = "SIN ESTUDIOS";

	private String correoFuncionario = "";
	private Tipo funcionarioEstado;
	private Tipo funcionarioCargo;
	private boolean vendedor;
	private boolean administrador;
	private boolean deposito;
	private boolean cobrador;
	private boolean telecobrador;
	private boolean cobradorInterior;
	private boolean tecnico;
	private boolean chofer;
	private boolean vendedorMostrador;
	private long idSucursal;
	
	private String estadoCivil;
	private String gradoAcademico;
	private int cantidadHijos;
	
	private double porc_comision = 0;
	private double porc_comision_cobros = 0;

	private String nombreEmpresa;
	private String nombreTecnico;
	
	private Date ultimoCambioPassword;
	
	private Empresa empresa;
	private Tecnico tecnico_;
	private Set<AccesoApp> accesos = new HashSet<AccesoApp>();
	private Set<VentaMeta> metas = new HashSet<VentaMeta>();
	private Set<FuncionarioPeriodoVacaciones> periodos = new HashSet<FuncionarioPeriodoVacaciones>();
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	/**
	 * @return la meta del funcionario en el mes corriente..
	 */
	public double getMetaActual() {
		return this.getMeta(Utiles.getNumeroMesCorriente());
	}
	
	/**
	 * @return la meta del funcionario en el mes..
	 */
	public double getMeta(int mes) {
		double out = 0;
		for (VentaMeta meta : this.metas) {
			if (meta.getPeriodo().equals(
					Utiles.getDateToString(new Date(), "yyyy"))) {
				switch (mes) {
				case Utiles.NRO_ENERO:
					return meta.getEnero();
				case Utiles.NRO_FEBRERO:
					return meta.getFebrero();
				case Utiles.NRO_MARZO:
					return meta.getMarzo();
				case Utiles.NRO_ABRIL:
					return meta.getAbril();
				case Utiles.NRO_MAYO:
					return meta.getMayo();
				case Utiles.NRO_JUNIO:
					return meta.getJunio();
				case Utiles.NRO_JULIO:
					return meta.getJulio();
				case Utiles.NRO_AGOSTO:
					return meta.getAgosto();
				case Utiles.NRO_SETIEMBRE:
					return meta.getSetiembre();
				case Utiles.NRO_OCTUBRE:
					return meta.getOctubre();
				case Utiles.NRO_NOVIEMBRE:
					return meta.getNoviembre();
				case Utiles.NRO_DICIEMBRE:
					return meta.getDiciembre();
				}
			}
		}
		return out;
	}
	
	/**
	 * @return el total de ventas del mes corriente..
	 */
	public double getTotalVentasActual() throws Exception {
		Calendar c = Calendar.getInstance(); 
	    c.set(Calendar.DAY_OF_MONTH, 1);
	    Date desde = c.getTime();
		return this.getTotalVentas(desde, new Date());
	}
	
	/**
	 * @return total de ventas sin iva segun rango..
	 */
	public double getTotalVentas(Date desde, Date hasta) throws Exception {
		double out = 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> ncs = rr.getNotasCredito_Venta_(desde, hasta, 0, this.getId().longValue());
		List<Object[]> vtas = rr.get_Ventas(desde, hasta, 0, this.getId().longValue());
		for (Object[] nc : ncs) {
			out -= ((double) nc[4] - Utiles.getIVA((double) nc[4], 10));
		}
		for (Object[] vta : vtas) {
			out += ((double) vta[6] - Utiles.getIVA((double) vta[6], 10));
		}		
		return out;
	}
	
	/**
	 * @return la definicion de comision..
	 * [0]: comision contado
	 * [1]: comision cobranzas
	 */
	public Object[] getPorcentajeComision(long idProveedor) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		VendedorComision com = rr.getVendedorComision(idProveedor, this.id);
		return com == null? new Object[]{ 0.0, 0.0 } : new Object[]{ com.getPorc_comision(), com.getPorc_comision_cobros() };
	}

	public String getCorreoFuncionario() {
		return correoFuncionario;
	}

	public void setCorreoFuncionario(String correoFuncionario) {
		this.correoFuncionario = correoFuncionario;
	}

	public Tipo getFuncionarioEstado() {
		return funcionarioEstado;
	}

	public void setFuncionarioEstado(Tipo funcionarioEstado) {
		this.funcionarioEstado = funcionarioEstado;
	}

	public Tipo getFuncionarioCargo() {
		return funcionarioCargo;
	}

	public void setFuncionarioCargo(Tipo funcionarioCargo) {
		this.funcionarioCargo = funcionarioCargo;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Set<AccesoApp> getAccesos() {
		return accesos;
	}

	public void setAccesos(Set<AccesoApp> accesos) {
		this.accesos = accesos;
	}

	public String getDescripcion() {
		return this.empresa.getNombre();
	}

	public void setDescripcion(String descripcion) {
	}
	
	public String getCedula() {
		return this.empresa.getCi();
	}
	
	public void setCedula(String cedula) {
	}
	
	public String getCodigoEmpresa() {
		return this.empresa.getCodigoEmpresa();
	}

	public void setCodigoEmpresa(String codigoEmpresa) {
	}
	
	public long getIdEmpresa() {
		return empresa.getId();
	}

	public void setIdEmpresa(long idEmpresa) {
	}

	public String getRazonSocial() {
		return this.empresa.getRazonSocial();
	}

	public void setRazonSocial(String razonSocial) {
	}
	
	public String getDireccion() {
		for (Sucursal sucursal : this.empresa.getSucursales()) {
			return sucursal.getDireccion();
		}
		return "Sin direccion..";
	}
	
	public void setDireccion(String direccion) {
	}
	
	public String getTelefono() {
		for (Sucursal sucursal : this.empresa.getSucursales()) {
			return sucursal.getTelefono();
		}
		return "...";
	}
	
	public void setTelefono(String direccion) {
	}
	
	public String getRuc() {
		return this.empresa.getRuc();
	}

	public void setRuc(String ruc) {
	}

	public boolean isVendedor() {
		return vendedor;
	}

	public void setVendedor(boolean vendedor) {
		this.vendedor = vendedor;
	}

	public Set<VentaMeta> getMetas() {
		return metas;
	}

	public void setMetas(Set<VentaMeta> metas) {
		this.metas = metas;
	}

	public boolean isAdministrador() {
		return administrador;
	}

	public void setAdministrador(boolean administrador) {
		this.administrador = administrador;
	}

	public boolean isDeposito() {
		return deposito;
	}

	public void setDeposito(boolean deposito) {
		this.deposito = deposito;
	}

	public boolean isCobrador() {
		return cobrador;
	}

	public void setCobrador(boolean cobrador) {
		this.cobrador = cobrador;
	}

	public boolean isTelecobrador() {
		return telecobrador;
	}

	public void setTelecobrador(boolean telecobrador) {
		this.telecobrador = telecobrador;
	}

	public double getPorc_comision() {
		return porc_comision;
	}

	public void setPorc_comision(double porc_comision) {
		this.porc_comision = porc_comision;
	}

	public double getPorc_comision_cobros() {
		return porc_comision_cobros;
	}

	public void setPorc_comision_cobros(double porc_comision_cobros) {
		this.porc_comision_cobros = porc_comision_cobros;
	}

	public boolean isTecnico() {
		return tecnico;
	}

	public void setTecnico(boolean tecnico) {
		this.tecnico = tecnico;
	}

	public String getNombreEmpresa() {
		return nombreEmpresa;
	}

	public void setNombreEmpresa(String nombreEmpresa) {
		this.nombreEmpresa = nombreEmpresa;
	}

	public boolean isChofer() {
		return chofer;
	}

	public void setChofer(boolean chofer) {
		this.chofer = chofer;
	}

	public boolean isVendedorMostrador() {
		return vendedorMostrador;
	}

	public void setVendedorMostrador(boolean vendedorMostrador) {
		this.vendedorMostrador = vendedorMostrador;
	}

	public long getIdSucursal() {
		return idSucursal;
	}

	public void setIdSucursal(long idSucursal) {
		this.idSucursal = idSucursal;
	}

	public String getNombreTecnico() {
		return nombreTecnico;
	}

	public void setNombreTecnico(String nombreTecnico) {
		this.nombreTecnico = nombreTecnico;
	}

	public Tecnico getTecnico_() {
		return tecnico_;
	}

	public void setTecnico_(Tecnico tecnico_) {
		this.tecnico_ = tecnico_;
	}

	public boolean isCobradorInterior() {
		return cobradorInterior;
	}

	public void setCobradorInterior(boolean cobradorInterior) {
		this.cobradorInterior = cobradorInterior;
	}

	public Set<FuncionarioPeriodoVacaciones> getPeriodos() {
		return periodos;
	}

	public void setPeriodos(Set<FuncionarioPeriodoVacaciones> periodos) {
		this.periodos = periodos;
	}

	public Date getUltimoCambioPassword() {
		return ultimoCambioPassword;
	}

	public void setUltimoCambioPassword(Date ultimoCambioPassword) {
		this.ultimoCambioPassword = ultimoCambioPassword;
	}

	public String getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(String estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public String getGradoAcademico() {
		return gradoAcademico;
	}

	public void setGradoAcademico(String gradoAcademico) {
		this.gradoAcademico = gradoAcademico;
	}

	public int getCantidadHijos() {
		return cantidadHijos;
	}

	public void setCantidadHijos(int cantidadHijos) {
		this.cantidadHijos = cantidadHijos;
	}
}
