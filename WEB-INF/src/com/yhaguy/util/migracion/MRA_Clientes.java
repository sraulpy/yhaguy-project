package com.yhaguy.util.migracion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import org.hibernate.Session;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.CondicionPago;
import com.yhaguy.domain.CtaCteEmpresa;
import com.yhaguy.domain.CtaCteLineaCredito;
import com.yhaguy.domain.CuentaContable;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.EmpresaGrupoSociedad;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.RucSet;
import com.yhaguy.domain.Sucursal;

public class MRA_Clientes {

	static String clientes = "./WEB-INF/docs/MigracionMariano/clientes-proveedores/clientes-Proveedores.csv";

	ArrayList<Object> empresasNuevas = new ArrayList<>();
	Hashtable<String, Object> empresasRucOk = new Hashtable<>();

	ArrayList<Object> empresasLista = new ArrayList<>();
	ArrayList<Object> clientesLista = new ArrayList<>();
	ArrayList<Object> proveedoresLista = new ArrayList<>();

	Misc m = new Misc();
	
	static RegisterDomain rr = RegisterDomain.getInstance();

	public MRA_Clientes() throws Exception {
	}

	static Tipo regimenTributarioExenta;
	static Tipo regimenTributarioNoExenta;
	static Tipo monedaDefault;
	static Tipo paisPy;
	static Tipo paisNoDefinido;
	static Tipo personaFisica;
	static Tipo personaJuridica;
	static Tipo categoriaClienteNodefinida;
	static Tipo tipoClienteNoDefinida;
	static Tipo estadoClienteActivo;
	static EmpresaGrupoSociedad grupoSociedadNoDefinido;

	static {

		try {

			// empresa grupo: Sin Grupo

			// regimen tributario: No Exenta
			regimenTributarioExenta = rr
					.getTipoPorSigla(Configuracion.SIGLA_REGIMEN_TRIB_EXENTA);
			regimenTributarioNoExenta = rr
					.getTipoPorSigla(Configuracion.SIGLA_REGIMEN_TRIB_NO_EXENTA);

			// pais: Py
			paisPy = rr.getTipoPorSigla(Configuracion.SIGLA_PAIS_PARAGUAY);
			paisNoDefinido = rr
					.getTipoPorSigla(Configuracion.SIGLA_PAIS_NO_DEFINIDO);

			// moneda: Gs
			monedaDefault = rr
					.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);

			// Persona física y jurídica
			personaFisica = rr.getTipoPorSigla("PF");
			personaJuridica = rr.getTipoPorSigla("PJ");

			// Categoria y tipo de cliente no definodos
			categoriaClienteNodefinida = rr.getTipoPorSigla_Index(
					Configuracion.SIGLA_CATEGORIA_CLIENTE, 1);
			tipoClienteNoDefinida = rr.getTipoPorSigla_Index(
					Configuracion.SIGLA_CLIENTE_TIPO_NO_DEFINIDO, 1);
			estadoClienteActivo = rr.getTipoPorSigla_Index(
					Configuracion.SIGLA_ESTADO_CLIENTE, 1);

			/*
			 * categoriaClienteNodefinida = rr.getCategoriaClienteById(1);
			 * tipoClienteNoDefinida = rr.getTipoClienteById(1);
			 * estadoClienteActivo = rr
			 * .getEstadoClienteByDescripcion(Configuracion
			 * .ESTADO_CLIENTE_ACTIVO);
			 */

			grupoSociedadNoDefinido = rr
					.getGrupoEmpresaByDescripcion(Configuracion.EMPRESA_GRUPO_NO_DEFINIDO);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void cargarClientesProveedores() throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();
		Session session = rr.SESSIONgetSession();
		
		CuentaContable ctCliente = rr.getCuentaContableByAlias_(Configuracion.ALIAS_CUENTA_CLIENTES_VARIOS);
		CuentaContable ctProve = rr.getCuentaContableByAlias_(Configuracion.ALIAS_CUENTA_PROVEEDORES_VARIOS);
		

		// abrir archivo
		File f = new File(clientes);
		BufferedReader entrada = new BufferedReader(new FileReader(f));

		String linea = "";
		// leer la cabecera
		linea = entrada.readLine() + " ";

		int ip = 0;
		while (entrada.ready()) {
			linea = entrada.readLine() + " ";
			String[] dato = linea.split("\t");
			System.out.println((ip++) + linea);

			// Cliente-Provee
			CliPro cliPro = new CliPro(dato);
			String[] rucRazon = SESSIONgetRazonSocialSET(cliPro.ruc, session);
			if (rucRazon == null) {
				cliPro.ruc = "";
				cliPro.observacion = "RUC NO VALIDADO \n" + cliPro.observacion;
				// crear empresa y cliente/proveedor
				Object objCliPro = this.creaEmpresaClienteProveedor(cliPro, ctCliente, ctProve);

				// agregar a la lista de empresas
				this.empresasNuevas.add(objCliPro);

			} else {
				cliPro.ruc = rucRazon[0];
				cliPro.razonSocial = rucRazon[1];

				// buscar si no está ya creado
				Object objCliPro = this.empresasRucOk.get(cliPro.ruc);

				if (objCliPro != null) {
					// si está creado, agregar sucursal (ya está en el hash
					this.agregaClienteProveedorSucursal(objCliPro, cliPro, ctCliente, ctProve);

				} else {
					// sino está creado,
					// crear empresa y cliente/proveedor
					objCliPro = this.creaEmpresaClienteProveedor(cliPro, ctCliente, ctProve);

					// agregar al HASH de empresas
					this.empresasRucOk.put(cliPro.ruc, objCliPro);
				}

			}

		}
		entrada.close();


		// grabar todo
		this.grabar("Empresa ", this.empresasLista, session);
		this.grabar("Cliente ", this.clientesLista, session);
		this.grabar("Proveedor ", this.proveedoresLista, session);

		System.out.println("--------");
		System.out.println("Empresas: " + this.empresasLista.size());
		System.out.println("Empresas RUC ok: " + this.empresasRucOk.size());
		System.out.println("Clientes: " + this.clientesLista.size());
		System.out.println("Proveedores: " + this.proveedoresLista.size());
		System.out.println("--------");
		
		
		
	}

	CondicionPago condicionPagoCliente = rr.getCondicionPagoById(1);
	Tipo estadoComoCliente = rr
			.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_SINCUENTA);
	Tipo estadoComoProveedor = rr
			.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_SINCUENTA);
	CtaCteLineaCredito lineaCredito = rr.getLineaCreditoById(1);

	private CtaCteEmpresa getCuentaCorriente() throws Exception {

		CtaCteEmpresa ctaCte = new CtaCteEmpresa();

		ctaCte.setCondicionPagoCliente(condicionPagoCliente);
		ctaCte.setEstadoComoCliente(estadoComoCliente);
		ctaCte.setEstadoComoProveedor(estadoComoProveedor);
		ctaCte.setFechaAperturaCuentaCliente(new Date());
		ctaCte.setFechaAperturaCuentaProveedor(new Date());
		ctaCte.setLineaCredito(lineaCredito);

		rr.saveObject(ctaCte, "MIGRA");

		return ctaCte;
	}

	private void grabar(String dd, ArrayList<Object> lista, Session session) {
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			int size = lista.size();
			for (int i = 0; i < size; i++) {
				System.out.println(dd + "  " + i + " / " + size);

				Domain dom = (Domain) lista.get(i);
				rr.saveObject(dom, "Migra");
				// rr.SESSIONsaveObjectDomain(dom, session, "MIGRA");

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void agregaClienteProveedorSucursal(Object obj, CliPro cliPro, CuentaContable ctCli, CuentaContable ctProv) {
		int tipo = 0;
		Empresa empresa = null;
		if (obj instanceof Cliente) {
			Cliente cliente = (Cliente) obj;
			cliente.setIdPersonaJedi(cliente.getIdPersonaJedi() + cliPro.codigo);
			cliente.setCuentaContable(ctCli);
			empresa = cliente.getEmpresa();
			tipo = 1;
		} else {
			Proveedor proveedor = (Proveedor) obj;
			proveedor.setAuxi(proveedor.getAuxi() + cliPro.codigo);
			proveedor.setCuentaContable(ctProv);
			empresa = proveedor.getEmpresa();
			tipo = 2;
		}

		empresa.setAuxi(empresa.getAuxi()+cliPro.codigo);
		empresa.setCodigoEmpresa(empresa.getCodigoEmpresa()+cliPro.codigo);
		
		// saber si agrega una sucursal
		if (tipo == cliPro.tipo) {
			// es el mismo tipo, agregar sucursal
			Sucursal sucursal = new Sucursal();
			sucursal.setDireccion(cliPro.direcciom);
			sucursal.setTelefono(cliPro.telefono);
			empresa.getSucursales().add(sucursal);
		} else {
			// hay que crear el cliente o proveedor
			if (cliPro.tipo == 1) {
				// cliente
				Cliente cliente = getCliente(cliPro.codigo);
				cliente.setEmpresa(empresa);
				cliente.setCuentaContable(ctCli);


			} else {
				// proveedor
				Proveedor proveedor = getProveedor(cliPro.codigo);
				proveedor.setEmpresa(empresa);
				proveedor.setCuentaContable(ctProv);


			}

		}

	}

	private Object creaEmpresaClienteProveedor(CliPro cliPro, CuentaContable ctCliente, CuentaContable ctProv) throws Exception {
		Object out = null;

		Empresa empresa = new Empresa();
		this.empresasLista.add(empresa);

		empresa.setRuc(cliPro.ruc);
		empresa.setRazonSocial(cliPro.razonSocial);
		empresa.setNombre(cliPro.nombre);
		empresa.setObservacion(cliPro.observacion);
		empresa.setAuxi(cliPro.codigo);
		empresa.setCodigoEmpresa(cliPro.codigo);

		empresa.setCtaCteEmpresa(getCuentaCorriente());
		empresa.setMoneda(monedaDefault);
		empresa.setEmpresaGrupoSociedad(grupoSociedadNoDefinido);
		empresa.setTipoPersona(personaFisica);
		if (m.esPersonaJuridica(cliPro.ruc) == true) {
			empresa.setTipoPersona(personaJuridica);
		}
		empresa.setPais(paisPy);
		


		Sucursal sucursal = new Sucursal();
		sucursal.setDireccion(cliPro.direcciom);
		sucursal.setTelefono(cliPro.telefono);

		empresa.getSucursales().add(sucursal);

		// si cliente o proveedor
		if (cliPro.tipo == 1) {
			// cliente
			Cliente cliente = getCliente(cliPro.codigo);
			cliente.setEmpresa(empresa);
			cliente.setCuentaContable(ctCliente);
			out = cliente;
		} else {
			// proveedor
			Proveedor proveedor = getProveedor(cliPro.codigo);
			proveedor.setEmpresa(empresa);
			proveedor.setCuentaContable(ctProv);
			out = proveedor;
		}
		return out;
	}

	
	private Cliente getCliente(String codigo){
		Cliente cliente = new Cliente();
		this.clientesLista.add(cliente);
		cliente.setIdPersonaJedi(codigo);
		
		cliente.setCategoriaCliente(categoriaClienteNodefinida);
		cliente.setTipoCliente(tipoClienteNoDefinida);

		// el listado es de sólo clientes activos
		cliente.setEstadoCliente(estadoClienteActivo);
		return cliente;
		
	}
	
	private Proveedor getProveedor(String codigo){
		Proveedor proveedor = new Proveedor();
		this.proveedoresLista.add(proveedor);

		proveedor.setAuxi(codigo);
		return proveedor;
	}
	
	public static void main(String[] args) throws Exception {

		MRA_Clientes cl = new MRA_Clientes();
		cl.cargarClientesProveedores();

	}

	// ========================================================
	// ========================================================
	// ========================================================

	public static String[] SESSIONgetRazonSocialSET(String ruc, Session session)
			throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();
		RucSet set = (RucSet) rr.SESSIONgetObject(RucSet.class.getName(),
				"ruc", ruc.trim(), session);
		if (set == null) {
			set = (RucSet) rr.SESSIONgetObject(RucSet.class.getName(),
					"rucOld", ruc.trim(), session);
		}
		if (set != null) {
			// encontro
			String rucSet = set.getRuc();
			String razonSocialSet = set.getRazonSocial();
			String[] rs = { rucSet, razonSocialSet };
			return rs;
		}
		return null;
	}

}

class CliPro {

	// 0 1 2 3 4 5 6
	// TIPO-1CL-2PRO CODIGO N O M B R E D I R E C C I O N ZONA TELEFONO RUC
	int tipo = 0;
	String codigo = "";
	String nombre = "";
	String razonSocial = "";
	String ruc = ";";
	String direcciom = "";
	String telefono = "";
	String observacion = "";

	public CliPro(String dato[]) {
		this.tipo = Integer.parseInt(dato[0].trim());
		if (this.tipo == 1){
			this.codigo = "-CL" + dato[1].trim() + "-";
		}else{
			this.codigo = "-PR" + dato[1].trim() + "-";
		}
		this.nombre = dato[2].trim();
		// en principio, razón social y nombre son iguales
		this.razonSocial = this.nombre;
		this.direcciom = dato[3].trim();
		this.telefono = dato[5].trim();
		this.ruc = dato[6].trim();
		this.observacion = dato[4].trim();
	}

}
