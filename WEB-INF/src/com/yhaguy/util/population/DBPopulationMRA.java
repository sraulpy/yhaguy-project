package com.yhaguy.util.population;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.domain.Usuario;
import com.coreweb.extras.csv.CSV;
import com.coreweb.util.AutoNumeroControl;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.AccesoApp;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloGasto;
import com.yhaguy.domain.ArticuloHistorialMigracion;
import com.yhaguy.domain.ArticuloListaPrecio;
import com.yhaguy.domain.ArticuloUbicacion;
import com.yhaguy.domain.Banco;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.CentroCosto;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.CtaCteLineaCredito;
import com.yhaguy.domain.CuentaContable;
import com.yhaguy.domain.DepartamentoApp;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.ProcesadoraTarjeta;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Reporte;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.domain.TipoCambio;
import com.yhaguy.domain.Vehiculo;
import com.yhaguy.util.migracion.MRA_Articulos;
import com.yhaguy.util.migracion.MRA_Clientes;
import com.yhaguy.util.migracion.MigracionPlanDeCuentas;
import com.yhaguy.util.migracion.mariano.MigracionMovimientosClientes;
import com.yhaguy.util.migracion.mariano.MigracionMovimientosProveedores;

public class DBPopulationMRA {
	
	/**
	 * Backup - Restore DB
	 * $ pg_dump -U {user-name} {source_db} -f {dumpfilename.sql}
	 * pg_dump -U postgres yhaguydb -f mradb.sql
	 * $ psql -U {user-name} -d {desintation_db}-f {dumpfilename.sql} 
	 * psql -U postgres -d yhaguydb -f mradb.sql
	 */

	/**
	 * Configuracion inicial de usuarios - funcionarios - accesos Mariano R. Alonso
	 */
	
	private static RegisterDomain rr = RegisterDomain.getInstance();
	private static DBPopulationTipos tt;
	private static Deposito dep1S1;
	private static Deposito depSC;
	static final String KEY_NRO = "VEH";
	
	private void dropTablas() throws Exception {
		rr.dropAllTables();
	}
	
	private void grabarDB(Domain d) throws Exception {
		rr.saveObject(d, Configuracion.USER_SYSTEMA);
	}
	
	private DBPopulationTipos cargarTipos() throws Exception {
		DBPopulationTipos tt = new DBPopulationTipos();
		tt.cargaTipos();
		return tt;
	}
	
	private void cargarPerfiles() throws Exception {
		UsuarioPerfilParser.loadMenuConfig();
	}
	
	/**
	 * Carga los ítems de Gastos
	 */
	public void cargarItemsDeGastos() throws Exception {
		System.out.println("----------------- Cargando Items de Gastos -------------------");
		
		List<CuentaContable> cuentas = rr.getCuentasContables();
		
		for (CuentaContable cuenta : cuentas) {
			ArticuloGasto artG = new ArticuloGasto();
			artG.setCreadoPor("Migracion");
			artG.setVerificadoPor("Migracion");
			artG.setCuentaContable(cuenta);
			artG.setDescripcion(cuenta.getDescripcion());
			grabarDB(artG);
			System.out.println("Item de Gasto: " + artG.getDescripcion());
		}
	}
	
	/**
	 * Población inicial de la BD..
	 */
	public void poblarBD() throws Exception {		
		
		System.out.println("-------------------- Poblando Tipos... --------------------");
		tt = this.cargarTipos();
		MigracionPlanDeCuentas.main(null);
		
		System.out.println("-------------------- Poblando Perfiles... --------------------");
		this.cargarPerfiles();
		
		Usuario noelia  = (Usuario) rr.getObject(Usuario.class.getName(), new Long(24));
		Usuario francis	= (Usuario) rr.getObject(Usuario.class.getName(), new Long(25));
		Usuario franciscom	= (Usuario) rr.getObject(Usuario.class.getName(), new Long(26));
		Usuario rody	= (Usuario) rr.getObject(Usuario.class.getName(), new Long(27));
		Usuario diana	= (Usuario) rr.getObject(Usuario.class.getName(), new Long(28));		
		Usuario userCentral	= (Usuario) rr.getObject(Usuario.class.getName(), new Long(29));
		Usuario wilfrido	= (Usuario) rr.getObject(Usuario.class.getName(), new Long(30));
		Usuario victor	= (Usuario) rr.getObject(Usuario.class.getName(), new Long(31));
		Usuario carlos	= (Usuario) rr.getObject(Usuario.class.getName(), new Long(32));
		Usuario edgar	= (Usuario) rr.getObject(Usuario.class.getName(), new Long(33));
		
		System.out.println("------------------- Poblando Funcionarios... -------------------");
		
		/** Un Depósito MRA **/
		
		depSC = new Deposito();
		depSC.setDescripcion("SC-D1");
		depSC.setObservacion("Depósito 1 Central");
		grabarDB(depSC);
		
		dep1S1 = new Deposito();
		dep1S1.setDescripcion("S1-D1");
		dep1S1.setObservacion("Depósito MRA");		
		grabarDB(dep1S1);
		
		Deposito dep2S1 = new Deposito();
		dep2S1.setDescripcion("S1-D2");
		dep2S1.setObservacion("Depósito 2 MRA");
		grabarDB(dep2S1);
		
		Deposito dep1S2 = new Deposito();
		dep1S2.setDescripcion("S2-D1");
		dep1S2.setObservacion("Depósito 1 Luber");
		grabarDB(dep1S2);
		
		Deposito dep1S3 = new Deposito();
		dep1S3.setDescripcion("S3-D1");
		dep1S3.setObservacion("Depósito 1 Fdo. de la Mora");
		grabarDB(dep1S3);
		
		Set<Deposito> depsSC = new HashSet<Deposito>();
		depsSC.add(depSC);
		
		Set<Deposito> depsS1 = new HashSet<Deposito>();
		depsS1.add(dep1S1);
		depsS1.add(dep2S1);
		
		Set<Deposito> depsS2 = new HashSet<Deposito>();
		depsS2.add(dep1S2);
		
		Set<Deposito> depsS3 = new HashSet<Deposito>();
		depsS3.add(dep1S3);				
		
		SucursalApp central = new SucursalApp();
		central.setNombre(Configuracion.ID_SUCURSALAPP_CENTRAL);
		central.setDescripcion(Configuracion.ID_SUCURSALAPP_CENTRAL);
		central.setEstablecimiento("1");
		central.setDireccion("");
		central.setTelefono("");
		central.setDepositos(depsSC);
		grabarDB(central);
		
		/** Una Sucursal **/
		SucursalApp suc1 = new SucursalApp();
		suc1.setNombre(Configuracion.ID_SUCURSALAPP_CENTRAL_MRA);
		suc1.setDescripcion(Configuracion.ID_SUCURSALAPP_CENTRAL_MRA);
		suc1.setEstablecimiento("2");
		suc1.setDireccion("");
		suc1.setTelefono("");
		suc1.setDepositos(depsS1);
		grabarDB(suc1);
		
		SucursalApp suc2 = new SucursalApp();
		suc2.setNombre(Configuracion.ID_SUCURSALAPP_MCAL_LOPEZ);
		suc2.setDescripcion(Configuracion.ID_SUCURSALAPP_MCAL_LOPEZ);
		suc2.setEstablecimiento("3");
		suc2.setDireccion("");
		suc2.setTelefono("");
		suc2.setDepositos(depsS2);
		grabarDB(suc2);
		
		SucursalApp suc3 = new SucursalApp();
		suc3.setNombre(Configuracion.ID_SUCURSALAPP_FDO);
		suc3.setDescripcion(Configuracion.ID_SUCURSALAPP_FDO);
		suc3.setEstablecimiento("4");
		suc3.setDireccion("");
		suc3.setTelefono("");
		suc3.setDepositos(depsS3);
		grabarDB(suc3);
		
		Set<SucursalApp> sucs = new HashSet<SucursalApp>();
		sucs.add(suc1);
		
		/** Un centro de costo **/
		Set<CentroCosto> ccs = new HashSet<CentroCosto>();
		String key = Configuracion.NRO_CENTRO_COSTO;
		
		CentroCosto cc = new CentroCosto();
		cc.setNumero(key + "-" + AutoNumeroControl.getAutoNumero(key, 5));
		cc.setDescripcion("ADMINISTRACIÓN - MRA");
		cc.setMontoAsignado(100000000);
		ccs.add(cc);
		
		/** Un Departamento Administrativo **/
		List<CuentaContable> cuentas = rr.getCuentasContables();
		DepartamentoApp dto1 = new DepartamentoApp();
		dto1.setNombre("ADMINISTRACIÓN");
		dto1.setDescripcion("Departamento de Administración");
		dto1.setSucursal(suc1);	
		dto1.setCentroCostos(ccs);
		dto1.setCuentas(new HashSet<CuentaContable>(cuentas));
		grabarDB(dto1);
		
		DepartamentoApp dto2 = new DepartamentoApp();
		dto2.setNombre("ADMIN-CENTRAL");
		dto2.setDescripcion("Departamento de Administración");
		dto2.setSucursal(central);	
		grabarDB(dto2);
		
		/** Tipos **/
		Tipo rubroFuncionario = rr.getTipoPorSigla(Configuracion.SIGLA_RUBRO_EMPRESA_FUNCIONARIO);
		Tipo monedaLocal = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);
		Tipo paraguay = rr.getTipoPorSigla(Configuracion.SIGLA_PAIS_PARAGUAY);
		Tipo personaFisica = rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_PERSONA_FISICA);
		Tipo auxiliarAdm = rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_CARGO_AUXILIAR_ADMINISTRATIVO);
		Tipo gerenteAdmi = rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_CARGO_GERENTE_ADMINISTRATIVO);
		Tipo estadoActivo = rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_FUNCIONARIO_ESTADO_ACTIVO);
		Tipo encargadoDepo = rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_CARGO_ENCARGADO_DEPOSITO);
		Tipo vendedorMostrador = rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_CARGO_VENTAS_MOSTRADOR);
		
		Set<Tipo> rubros = new HashSet<Tipo>();
		rubros.add(rubroFuncionario);
		
		Set<Tipo> monedas = new HashSet<Tipo>();		
		
		
		/***************** Configuración usuario Noelia Leon ****************/
		
		Empresa empNoelia = new Empresa();
		empNoelia.setNombre("León, Noelia");
		empNoelia.setCodigoEmpresa("0");
		empNoelia.setFechaIngreso(new Date());
		empNoelia.setObservacion("sin obs");
		empNoelia.setRazonSocial("Noelia León");
		empNoelia.setRubroEmpresas(rubros);
		empNoelia.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empNoelia.setMoneda(monedaLocal);
		empNoelia.setMonedas(monedas);
		empNoelia.setPais(paraguay);
		empNoelia.setSigla("NDF");
		empNoelia.setTipoPersona(personaFisica);
		grabarDB(empNoelia);
		
		AccesoApp accNoelia = new AccesoApp();
		accNoelia.setDescripcion("Acceso Noelia León");
		accNoelia.setDepartamento(dto1);
		accNoelia.setSucursales(sucs);
		accNoelia.setUsuario(noelia);		
		grabarDB(accNoelia);
		
		Set<AccesoApp> accsNoelia = new HashSet<AccesoApp>();
		accsNoelia.add(accNoelia);
		
		Funcionario funNoelia = new Funcionario();
		funNoelia.setEmpresa(empNoelia);
		funNoelia.setFuncionarioCargo(gerenteAdmi);
		funNoelia.setFuncionarioEstado(estadoActivo);
		funNoelia.setAccesos(accsNoelia);
		grabarDB(funNoelia);
				
		/*************** Configuración usuario Francisco Recalde ***************/
		
		Empresa empFrancisco = new Empresa();
		empFrancisco.setNombre("Recalde, Francisco");
		empFrancisco.setCodigoEmpresa("0");
		empFrancisco.setFechaIngreso(new Date());
		empFrancisco.setObservacion("sin obs");
		empFrancisco.setRazonSocial("Francisco Recalde");
		empFrancisco.setRubroEmpresas(rubros);
		empFrancisco.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empFrancisco.setMoneda(monedaLocal);
		empFrancisco.setMonedas(monedas);
		empFrancisco.setPais(paraguay);
		empFrancisco.setSigla("NDF");
		empFrancisco.setTipoPersona(personaFisica);
		grabarDB(empFrancisco);
		
		AccesoApp accFrancisco = new AccesoApp();
		accFrancisco.setDescripcion("Acceso Francisco Recalde");
		accFrancisco.setDepartamento(dto1);
		accFrancisco.setSucursales(sucs);
		accFrancisco.setUsuario(francis);		
		grabarDB(accFrancisco);
		
		Set<AccesoApp> accsFrancisco = new HashSet<AccesoApp>();
		accsFrancisco.add(accFrancisco);
		
		Funcionario funFrancisco = new Funcionario();
		funFrancisco.setEmpresa(empFrancisco);
		funFrancisco.setFuncionarioCargo(auxiliarAdm);
		funFrancisco.setFuncionarioEstado(estadoActivo);
		funFrancisco.setAccesos(accsFrancisco);
		grabarDB(funFrancisco);
		
		/***********************************************************************/
		
		
		/************** Configuración usuario Francisco Mattesich **************/
		
		Empresa empFranciscom = new Empresa();
		empFranciscom.setNombre("Mattesich, Francisco");
		empFranciscom.setCodigoEmpresa("0");
		empFranciscom.setFechaIngreso(new Date());
		empFranciscom.setObservacion("sin obs");
		empFranciscom.setRazonSocial("Francisco Mattesich");
		empFranciscom.setRubroEmpresas(rubros);
		empFranciscom.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empFranciscom.setMoneda(monedaLocal);
		empFranciscom.setMonedas(monedas);
		empFranciscom.setPais(paraguay);
		empFranciscom.setSigla("NDF");
		empFranciscom.setTipoPersona(personaFisica);
		grabarDB(empFranciscom);
		
		AccesoApp accFranciscom = new AccesoApp();
		accFranciscom.setDescripcion("Acceso Francisco Mattesich");
		accFranciscom.setDepartamento(dto1);
		accFranciscom.setSucursales(sucs);
		accFranciscom.setUsuario(franciscom);		
		grabarDB(accFranciscom);
		
		Set<AccesoApp> accsFranciscom = new HashSet<AccesoApp>();
		accsFranciscom.add(accFranciscom);
		
		Funcionario funFranciscom = new Funcionario();
		funFranciscom.setEmpresa(empFranciscom);
		funFranciscom.setFuncionarioCargo(encargadoDepo);
		funFranciscom.setFuncionarioEstado(estadoActivo);
		funFranciscom.setAccesos(accsFranciscom);
		grabarDB(funFranciscom);
		
		/***************** Configuración usuario Yhaguy Central ****************/

		Empresa empCentral = new Empresa();
		empCentral.setNombre("Usuario Central");
		empCentral.setCodigoEmpresa("0");
		empCentral.setFechaIngreso(new Date());
		empCentral.setObservacion("sin obs");
		empCentral.setRazonSocial("Usuario Central");
		empCentral.setRubroEmpresas(rubros);
		empCentral.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empCentral.setMoneda(monedaLocal);
		empCentral.setMonedas(monedas);
		empCentral.setPais(paraguay);
		empCentral.setSigla("NDF");
		empCentral.setTipoPersona(personaFisica);
		grabarDB(empCentral);

		AccesoApp accCentral = new AccesoApp();
		accCentral.setDescripcion("Acceso Usuario Central");
		accCentral.setDepartamento(dto2);
		accCentral.setSucursales(sucs);
		accCentral.setUsuario(userCentral);
		grabarDB(accCentral);

		Set<AccesoApp> accsCentral = new HashSet<AccesoApp>();
		accsCentral.add(accCentral);

		Funcionario funCentral = new Funcionario();
		funCentral.setEmpresa(empCentral);
		funCentral.setFuncionarioCargo(encargadoDepo);
		funCentral.setFuncionarioEstado(estadoActivo);
		funCentral.setAccesos(accsCentral);
		grabarDB(funCentral);
		
		/*************** Configuración usuario Rody Guerrero ***************/
		
		Empresa empRody = new Empresa();
		empRody.setNombre("Guerrero, Rody");
		empRody.setCodigoEmpresa("0");
		empRody.setFechaIngreso(new Date());
		empRody.setObservacion("sin obs");
		empRody.setRazonSocial("Rody Guerrero");
		empRody.setRubroEmpresas(rubros);
		empRody.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empRody.setMoneda(monedaLocal);
		empRody.setMonedas(monedas);
		empRody.setPais(paraguay);
		empRody.setSigla("NDF");
		empRody.setTipoPersona(personaFisica);
		grabarDB(empRody);
		
		AccesoApp accRody = new AccesoApp();
		accRody.setDescripcion("Rody Guerrero");
		accRody.setDepartamento(dto1);
		accRody.setSucursales(sucs);
		accRody.setUsuario(rody);		
		grabarDB(accRody);
		
		Set<AccesoApp> accsRody = new HashSet<AccesoApp>();
		accsRody.add(accRody);
		
		Funcionario funRody = new Funcionario();
		funRody.setEmpresa(empRody);
		funRody.setFuncionarioCargo(encargadoDepo);
		funRody.setFuncionarioEstado(estadoActivo);
		funRody.setAccesos(accsRody);
		grabarDB(funRody);		
		
		/*************** Configuración usuario Diana Marin *********************/
		
		Empresa empDiana = new Empresa();
		empDiana.setNombre("Marín, Diana");
		empDiana.setCodigoEmpresa("0");
		empDiana.setFechaIngreso(new Date());
		empDiana.setObservacion("sin obs");
		empDiana.setRazonSocial("Diana Marín");
		empDiana.setRubroEmpresas(rubros);
		empDiana.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empDiana.setMoneda(monedaLocal);
		empDiana.setMonedas(monedas);
		empDiana.setPais(paraguay);
		empDiana.setSigla("NDF");
		empDiana.setTipoPersona(personaFisica);
		grabarDB(empDiana);
		
		AccesoApp accDiana = new AccesoApp();
		accDiana.setDescripcion("Diana Marín");
		accDiana.setDepartamento(dto1);
		accDiana.setSucursales(sucs);
		accDiana.setUsuario(diana);		
		grabarDB(accDiana);
		
		Set<AccesoApp> accsDiana = new HashSet<AccesoApp>();
		accsDiana.add(accDiana);
		
		Funcionario funDiana = new Funcionario();
		funDiana.setEmpresa(empDiana);
		funDiana.setFuncionarioCargo(encargadoDepo);
		funDiana.setFuncionarioEstado(estadoActivo);
		funDiana.setAccesos(accsDiana);
		grabarDB(funDiana);
		
		/********************** Configuración usuario Wilfrido **********************/
		
		Empresa empWilfrido = new Empresa();
		empWilfrido.setNombre("Valenzuela, Wilfrido");
		empWilfrido.setCodigoEmpresa("0");
		empWilfrido.setFechaIngreso(new Date());
		empWilfrido.setObservacion("sin obs");
		empWilfrido.setRazonSocial("Wilfrido Valenzuela");
		empWilfrido.setRubroEmpresas(rubros);
		empWilfrido.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empWilfrido.setMoneda(monedaLocal);
		empWilfrido.setMonedas(monedas);
		empWilfrido.setPais(paraguay);
		empWilfrido.setSigla("NDF");
		empWilfrido.setTipoPersona(personaFisica);
		grabarDB(empWilfrido);
		
		AccesoApp accWilfrido = new AccesoApp();
		accWilfrido.setDescripcion("Acceso Wilfrido Valenzuela");
		accWilfrido.setDepartamento(dto1);
		accWilfrido.setSucursales(sucs);
		accWilfrido.setUsuario(wilfrido);		
		grabarDB(accWilfrido);
		
		Set<AccesoApp> accsWilfrido = new HashSet<AccesoApp>();
		accsWilfrido.add(accWilfrido);
		
		Funcionario funWilfrido = new Funcionario();
		funWilfrido.setEmpresa(empWilfrido);
		funWilfrido.setFuncionarioCargo(vendedorMostrador);
		funWilfrido.setFuncionarioEstado(estadoActivo);
		funWilfrido.setAccesos(accsWilfrido);
		grabarDB(funWilfrido);		
		
		/********************** Configuración usuario Victor **********************/
		
		Empresa empVictor = new Empresa();
		empVictor.setNombre("Amarilla, Victor");
		empVictor.setCodigoEmpresa("0");
		empVictor.setFechaIngreso(new Date());
		empVictor.setObservacion("sin obs");
		empVictor.setRazonSocial("Victor Amarilla");
		empVictor.setRubroEmpresas(rubros);
		empVictor.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empVictor.setMoneda(monedaLocal);
		empVictor.setMonedas(monedas);
		empVictor.setPais(paraguay);
		empVictor.setSigla("NDF");
		empVictor.setTipoPersona(personaFisica);
		grabarDB(empVictor);
		
		AccesoApp accVictor = new AccesoApp();
		accVictor.setDescripcion("Acceso Victor Amarilla");
		accVictor.setDepartamento(dto1);
		accVictor.setSucursales(sucs);
		accVictor.setUsuario(victor);		
		grabarDB(accVictor);
		
		Set<AccesoApp> accsVictor = new HashSet<AccesoApp>();
		accsVictor.add(accVictor);
		
		Funcionario funVictor = new Funcionario();
		funVictor.setEmpresa(empVictor);
		funVictor.setFuncionarioCargo(vendedorMostrador);
		funVictor.setFuncionarioEstado(estadoActivo);
		funVictor.setAccesos(accsVictor);
		grabarDB(funVictor);
		
		/********************** Configuración usuario Carlos **********************/
		
		Empresa empCarlos = new Empresa();
		empCarlos.setNombre("López, Carlos");
		empCarlos.setCodigoEmpresa("0");
		empCarlos.setFechaIngreso(new Date());
		empCarlos.setObservacion("sin obs");
		empCarlos.setRazonSocial("Carlos López");
		empCarlos.setRubroEmpresas(rubros);
		empCarlos.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empCarlos.setMoneda(monedaLocal);
		empCarlos.setMonedas(monedas);
		empCarlos.setPais(paraguay);
		empCarlos.setSigla("NDF");
		empCarlos.setTipoPersona(personaFisica);
		grabarDB(empCarlos);
		
		AccesoApp accCarlos = new AccesoApp();
		accCarlos.setDescripcion("Acceso Carlos López");
		accCarlos.setDepartamento(dto1);
		accCarlos.setSucursales(sucs);
		accCarlos.setUsuario(carlos);		
		grabarDB(accCarlos);
		
		Set<AccesoApp> accsCarlos = new HashSet<AccesoApp>();
		accsCarlos.add(accCarlos);
		
		Funcionario funCarlos = new Funcionario();
		funCarlos.setEmpresa(empCarlos);
		funCarlos.setFuncionarioCargo(vendedorMostrador);
		funCarlos.setFuncionarioEstado(estadoActivo);
		funCarlos.setAccesos(accsCarlos);
		grabarDB(funCarlos);
		
		/********************** Configuración usuario Edgar **********************/
		
		Empresa empEdgar = new Empresa();
		empEdgar.setNombre("Roa, Edgar");
		empEdgar.setCodigoEmpresa("0");
		empEdgar.setFechaIngreso(new Date());
		empEdgar.setObservacion("sin obs");
		empEdgar.setRazonSocial("Edgar Roa");
		empEdgar.setRubroEmpresas(rubros);
		empEdgar.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empEdgar.setMoneda(monedaLocal);
		empEdgar.setMonedas(monedas);
		empEdgar.setPais(paraguay);
		empEdgar.setSigla("NDF");
		empEdgar.setTipoPersona(personaFisica);
		grabarDB(empEdgar);
		
		AccesoApp accEdgar = new AccesoApp();
		accEdgar.setDescripcion("Edgar Roa");
		accEdgar.setDepartamento(dto1);
		accEdgar.setSucursales(sucs);
		accEdgar.setUsuario(edgar);		
		grabarDB(accEdgar);
		
		Set<AccesoApp> accsEdgar = new HashSet<AccesoApp>();
		accsEdgar.add(accEdgar);
		
		Funcionario funEdgar = new Funcionario();
		funEdgar.setEmpresa(empEdgar);
		funEdgar.setFuncionarioCargo(vendedorMostrador);
		funEdgar.setFuncionarioEstado(estadoActivo);
		funEdgar.setAccesos(accsEdgar);
		grabarDB(funEdgar);
	
	
		/************************* Clientes Ocasionales ************************/
		
		String siglaClienteActivo = Configuracion.SIGLA_ESTADO_CLIENTE;
		String siglaCatCliAldia = Configuracion.SIGLA_CATEGORIA_CLIENTE;
		String aliasCtCliOcasional = Configuracion.ALIAS_CUENTA_CLIENTES_OCASIONALES;
		String codigoCliOcasional = Configuracion.CODIGO_CLIENTE_OCASIONAL_CL;
		String rucEmpresaLocal = Configuracion.RUC_EMPRESA_LOCAL;
		
		Tipo estadoClienteActivo = rr.getTipoPorSigla_Index(siglaClienteActivo, 1);
		Tipo categoriaClienteAlDia = rr.getTipoPorSigla_Index(siglaCatCliAldia, 1);
		Tipo tipoClienteNoDefinido = tt.tipoCliente1;
		CuentaContable ctClienteOcasional = rr.getCuentaContableByAlias_(aliasCtCliOcasional);

		Empresa empClienteOcasional = new Empresa();
		empClienteOcasional.setCodigoEmpresa(codigoCliOcasional);
		empClienteOcasional.setNombre("Cliente Ocasional");
		empClienteOcasional.setFechaIngreso(new Date());
		empClienteOcasional.setObservacion("");
		empClienteOcasional.setRazonSocial("Cliente Ocasional");
		empClienteOcasional.setRuc(rucEmpresaLocal);
		empClienteOcasional.setPais(paraguay);
		empClienteOcasional.setSigla("NDF");
		empClienteOcasional.setTipoPersona(personaFisica);
		grabarDB(empClienteOcasional);

		Cliente clienteOcasional = new Cliente();
		clienteOcasional.setEstadoCliente(estadoClienteActivo);
		clienteOcasional.setCategoriaCliente(categoriaClienteAlDia);
		clienteOcasional.setTipoCliente(tipoClienteNoDefinido);
		clienteOcasional.setEmpresa(empClienteOcasional);
		clienteOcasional.setCuentaContable(ctClienteOcasional);
		grabarDB(clienteOcasional);
		
		/***********************************************************************/
		
		
		/********************* Datos Utilizados en Cta. Cte. *******************/
		
		/**
		 * Los datos correspondientes a las lineas de creditos utilizadas en MRA.
		 */
		CtaCteLineaCredito l0 = new CtaCteLineaCredito();
		l0.setLinea(200000);
		l0.setDescripcion("A");
		grabarDB(l0);
		
		CtaCteLineaCredito l1 = new CtaCteLineaCredito();
		l1.setLinea(500000);
		l1.setDescripcion("B");
		grabarDB(l1);
		
		CtaCteLineaCredito l2 = new CtaCteLineaCredito();
		l2.setLinea(1000000);
		l2.setDescripcion("C");
		grabarDB(l2);
		
		CtaCteLineaCredito l3 = new CtaCteLineaCredito();
		l3.setLinea(2000000);
		l3.setDescripcion("D");
		grabarDB(l3);

		CtaCteLineaCredito l4 = new CtaCteLineaCredito();
		l4.setLinea(3000000);
		l4.setDescripcion("E");
		grabarDB(l4);
		
		CtaCteLineaCredito l5 = new CtaCteLineaCredito();
		l5.setLinea(5000000);
		l5.setDescripcion("F");
		grabarDB(l5);
		
		CtaCteLineaCredito l6 = new CtaCteLineaCredito();
		l6.setLinea(7000000);
		l6.setDescripcion("G");
		grabarDB(l6);
		
		CtaCteLineaCredito l7 = new CtaCteLineaCredito();
		l7.setLinea(8000000);
		l7.setDescripcion("H");
		grabarDB(l7);
		
		CtaCteLineaCredito l8 = new CtaCteLineaCredito();
		l8.setLinea(10000000);
		l8.setDescripcion("I");
		grabarDB(l8);
		
		CtaCteLineaCredito l9 = new CtaCteLineaCredito();
		l9.setLinea(12000000);
		l9.setDescripcion("J");
		grabarDB(l9);
		
		CtaCteLineaCredito l10 = new CtaCteLineaCredito();
		l10.setLinea(15000000);
		l10.setDescripcion("K");
		grabarDB(l10);
		
		CtaCteLineaCredito l11 =  new CtaCteLineaCredito();
		l11.setLinea(18000000);
		l11.setDescripcion("L");
		grabarDB(l11);
		
		CtaCteLineaCredito l12 = new CtaCteLineaCredito();
		l12.setLinea(20000000);
		l12.setDescripcion("M");
		grabarDB(l12);
		
		CtaCteLineaCredito l13 = new CtaCteLineaCredito();
		l13.setLinea(30000000);
		l13.setDescripcion("N");
		grabarDB(l13);
		
		/**
		 * El tipo de cambio del Guarani que siempre es igual a '1' tanto para App y para BCP
		 */		
		TipoCambio tc1 = new TipoCambio();
		tc1.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		tc1.setTipoCambio(rr.getTipoPorDescripcion(Configuracion.ID_TIPO_CAMBIO_APP));
		tc1.setFecha(new Date());
		tc1.setCompra(1);
		tc1.setVenta(1);
		grabarDB(tc1);
		
		TipoCambio tc2 = new TipoCambio();
		tc2.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		tc2.setTipoCambio(rr.getTipoPorDescripcion(Configuracion.ID_TIPO_CAMBIO_BCP));
		tc2.setFecha(new Date());
		tc2.setCompra(1);
		tc2.setVenta(1);
		grabarDB(tc2);
		
		/**
		 * Tipo de Cambio para moneda Dolar..
		 */
		TipoCambio tc3 = new TipoCambio();
		tc3.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_DOLAR));
		tc3.setTipoCambio(rr.getTipoPorDescripcion(Configuracion.ID_TIPO_CAMBIO_BCP));
		tc3.setFecha(new Date());
		tc3.setCompra(5100);
		tc3.setVenta(5100);
		grabarDB(tc3);
		
		
		/*************************** Banco**********************************/
		Banco bc01 = new Banco();
		bc01.setDescripcion("Continental");
		bc01.setBancoTipo(rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_BANCOS_TERCEROS_CONTI));
		grabarDB(bc01);
		
		Banco bc02 = new Banco();
		bc02.setDescripcion("Itaú");
		bc02.setBancoTipo(rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_BANCOS_TERCEROS_ITAU));
		grabarDB(bc02);

		BancoCta bcc01 = new BancoCta();
		bcc01.setBanco(bc01);
		bcc01.setFechaApertura(new Date());
		bcc01.setMoneda(tt.guarani);
		bcc01.setNroCuenta("06.485.580-08");
		bcc01.setTipo(tt.bancoCtaCte);
		bcc01.setCuentaContable(rr.getCuentaContableByAlias_("CT-BANCO-CONTINENTAL-MRA"));
		
		grabarDB(bcc01);
		
		BancoCta bcc02 = new BancoCta();
		bcc02.setBanco(bc02);
		bcc02.setFechaApertura(new Date());
		bcc02.setMoneda(tt.guarani);
		bcc02.setNroCuenta("80027742-2");
		bcc02.setTipo(tt.bancoCtaCte);
		bcc02.setCuentaContable(rr.getCuentaContableByAlias_("CT-BANCO-ITAU-MRA"));
		grabarDB(bcc02);
		
		
		/********************* Articulos de referencia *************************/
		
		String[] refs = { "@GASTOS", "@DESCUENTO", "@PRORRATEO" };
		
		for (int i = 0; i < refs.length; i++) {
			
			Articulo ref = new Articulo();
			ref.setDescripcion(refs[i]);
			ref.setCodigoInterno(refs[i]);
			ref.setCodigoOriginal(refs[i]);
			ref.setCodigoBarra(refs[i]);
			ref.setObservacion("");
			ref.setArticuloMarca(tt.sinReferenciaTipo);
			ref.setArticuloParte(tt.sinReferenciaTipo);
			ref.setArticuloLinea(tt.sinReferenciaTipo);
			ref.setArticuloFamilia(tt.sinReferenciaTipo);
			ref.setArticuloEstado(tt.estadoArticulo1);
			ref.setArticuloPresentacion(rr.getArticuloPresentacionById(1));
			ref.setImportado(false);
			
			grabarDB(ref);
		}
		
		
		/********************* Procesadoras de Tarjetas *************************/
		
		String[] procs = { "Procard", "Panal", "Bancard", "Cabal" };
		
		for (int i = 0; i < procs.length; i++) {
			
			ProcesadoraTarjeta pr = new ProcesadoraTarjeta();
			pr.setNombre(procs[i]);
			pr.setBanco(bcc01);			
			pr.setSucursal(suc1);			
			grabarDB(pr);
		}
		
		
		/**************************** Listas de Precio ***************************/
		
		ArticuloListaPrecio minorista = new ArticuloListaPrecio();
		minorista.setDescripcion("MINORISTA");
		minorista.setMargen(60);
		minorista.setActivo(true);
		grabarDB(minorista);
		
		ArticuloListaPrecio mayorista = new ArticuloListaPrecio();
		mayorista.setDescripcion("MAYORISTA");
		mayorista.setMargen(30);
		mayorista.setActivo(true);
		grabarDB(mayorista);
		
		
		/**************************** Vehículos ***************************/
		this.poblarVEH(suc1);
		
		/**************************** Ubicaciones ***************************/
		this.poblarAU();
		
		// Reportes
		this.poblarDefinicionReportes();
	}	
	
	/**
	 * Poblar vehículos MRA
	 * @throws Exception 
	 */
	
	public void poblarVEH(SucursalApp suc2) throws Exception {
		
		System.out.println("-------------------- Poblando Vehículos... --------------------");
		
		Vehiculo veh1 = new Vehiculo();
		veh1.setCodigo(AutoNumeroControl.getAutoNumeroKey(KEY_NRO,4));
		veh1.setMarca("KENTON/Moto");
		veh1.setModelo("GL/25/2014");
		veh1.setChapa("566 BGU");
		veh1.setColor("Rojo");
		veh1.setSucursal(suc2);
		grabarDB(veh1);
		
		Vehiculo veh2 = new Vehiculo();
		veh2.setCodigo(AutoNumeroControl.getAutoNumeroKey(KEY_NRO,4));
		veh2.setMarca("FIAT");
		veh2.setModelo("2012-Fioreno");
		veh2.setChapa("B4P 937");
		veh2.setColor("Blanco");
		veh2.setSucursal(suc2);
		grabarDB(veh2);
		
		Vehiculo veh3 = new Vehiculo();
		veh3.setCodigo(AutoNumeroControl.getAutoNumeroKey(KEY_NRO,4));
		veh3.setMarca("CHANGAN");
		veh3.setModelo("SC1024GDD41/2014");
		veh3.setChapa("OBO348");
		veh3.setColor("Blanco");
		veh3.setSucursal(suc2);
		grabarDB(veh3);
		
	}
	
	
	/**
	 * Poblar Articulo Ubicaciones MRA
	 * @throws Exception 
	 */
	public void poblarAU() throws Exception {
		System.out.println("-------------------- Poblando Ubicaciones... --------------------");
		
		ArticuloUbicacion au;
		String[] estante = { "A", "B", "C", "D", "E", "F", "G", "H",
				"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
				"U", "V", "W", "X", "Y", "Z" };
		for(String est : estante){
			for(int f=1; f<4; f++){
				for(int c=1; c<10; c++){
					au = new ArticuloUbicacion();
					au.setEstante(est);
					au.setFila(String.valueOf(f));
					au.setColumna(String.valueOf(c));
					grabarDB(au);
				}
			}
		}
		
		
	}
	
	/**
	 * pobla la definicion de reportes del sistema..
	 */
	private void poblarDefinicionReportes() throws Exception {
		System.out
				.println("-------------------- Poblando Reportes... --------------------");

		String[] tesoreria = new String[] {
				"[7] - Pagos por Cliente y Fecha de Pago",
				"[8] - Listado de Retenciones por fecha",
				"[10] - Listado de Saldos de Clientes",
				"[11] - Listado de Saldos de Clientes detallado",
				"[13] - Listado de Clientes sin Movimiento",
				"[24] - Verificación de Pagos con Cheques",
				"[25] - Detalles de Pagos del Periodo",
				"[26] - Pagos por Número de Recibo",
				"Estado de Cuenta de Proveedores",
				"Estado de Cuenta de Clientes",
				"Pagos por Proveedor", "[4] - Cheques a vencer",
				"[5] - Cheques por Cliente",
				"[6] - Cheques por Número de comprobante",
				"[7] - Cheques descontados" };

		String[] compras = new String[] { "[2] - Listado de Compras" };
		
		String[] ventas = new String[] { "Listado de Ventas y Notas de Crédito genérico",
				"[10] - Ventas y Utilidad",
				"[13] - Listado de Ventas por código de operación",
				"[19] - Ventas por Vendedor",
				"[21] - Ventas y cobranzas por Vendedor" };
		
		String[] stock = new String[] { "[21] - Articulos genérico" };
		
		String[] logistica = new String[] { "Listado de Repartos" };
		
		String[] contabilidad = new String[] { "Libro Compras",
				"[11-12] Libro Ventas", "[27] - Ventas Hechauka",
				"[28] - Retenciones Hechauka" };
		
		String[] rrhh = new String[] { "[3] - Liquidación de vendedores por ventas" };
		
		String[] sistema = new String[] { "Listado de Usuarios" };

		for (String tes : tesoreria) {
			Reporte rep = new Reporte();
			rep.setCodigo("TES-" + AutoNumeroControl.getAutoNumero("TES", 5));
			rep.setDescripcion(tes);
			rep.setModulo(1);
			grabarDB(rep);
		}

		for (String comp : compras) {
			Reporte rep = new Reporte();
			rep.setCodigo("COM-" + AutoNumeroControl.getAutoNumero("COM", 5));
			rep.setDescripcion(comp);
			rep.setModulo(2);
			grabarDB(rep);
		}

		for (String ven : ventas) {
			Reporte rep = new Reporte();
			rep.setCodigo("VEN-" + AutoNumeroControl.getAutoNumero("VEN", 5));
			rep.setDescripcion(ven);
			rep.setModulo(3);
			grabarDB(rep);
		}

		for (String st : stock) {
			Reporte rep = new Reporte();
			rep.setCodigo("STK-" + AutoNumeroControl.getAutoNumero("STK", 5));
			rep.setDescripcion(st);
			rep.setModulo(4);
			grabarDB(rep);
		}

		for (String log : logistica) {
			Reporte rep = new Reporte();
			rep.setCodigo("LOG-" + AutoNumeroControl.getAutoNumero("LOG", 5));
			rep.setDescripcion(log);
			rep.setModulo(5);
			grabarDB(rep);
		}

		for (String cont : contabilidad) {
			Reporte rep = new Reporte();
			rep.setCodigo("CON-" + AutoNumeroControl.getAutoNumero("CON", 5));
			rep.setDescripcion(cont);
			rep.setModulo(6);
			grabarDB(rep);
		}

		for (String rh : rrhh) {
			Reporte rep = new Reporte();
			rep.setCodigo("RRHH-" + AutoNumeroControl.getAutoNumero("RRHH", 5));
			rep.setDescripcion(rh);
			rep.setModulo(7);
			grabarDB(rep);
		}

		for (String sis : sistema) {
			Reporte rep = new Reporte();
			rep.setCodigo("SIS-" + AutoNumeroControl.getAutoNumero("SIS", 5));
			rep.setDescripcion(sis);
			rep.setModulo(8);
			grabarDB(rep);
		}
	}
	
	public void poblarArticuloHistorialMigracion() throws Exception{
		
		String src = "./WEB-INF/docs/MigracionMariano/Articulos/articulos.csv";
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CODIGO", CSV.STRING },
				{ "DESCRIPCION", CSV.STRING }, { "COSTO", CSV.NUMERICO },
				{ "PRECIO", CSV.NUMERICO }, { "STOCK", CSV.NUMERICO } };

		System.out.println("---------------------- Poblando Articulos ----------------------");

		CSV csv = new CSV(cab, det, src);

		csv.start();
		int cont = 0;
		while (csv.hashNext()) {

			String codigo = csv.getDetalleString("CODIGO");
			String codProveedor = "";
			String codOriginal = "";
			String descripcion = csv.getDetalleString("DESCRIPCION");
			double costo = csv.getDetalleDouble("COSTO");
			double precio = csv.getDetalleDouble("PRECIO");
			int stock = ((Float)csv.getDetalle("STOCK")).intValue();
			
			System.out.println(codigo + " - " + descripcion + " - " + costo
					+ " - " + precio + " - " + stock );
			Articulo art = rr.getArticulo(codigo);
						
			if(art != null){
				ArticuloHistorialMigracion artMra = new ArticuloHistorialMigracion();
				artMra.setFechaAlta(art.getModificado());
				artMra.setDescripcion(descripcion);
				artMra.setCodigoInterno(codigo);
				artMra.setCodigoProveedor(codProveedor);
				artMra.setCodigoOriginal(codOriginal);
				artMra.setCodigoBarra(codigo);
				artMra.setImportado(true);
				artMra.setStock(stock);
				artMra.setCosto(costo);
				artMra.setSucursal(rr.getSucursalAppById(2));
				rr.saveObject(artMra, "Migracion");
			} else {
				cont++;
			}			
		}		
		System.out.println("------------- Art no encontrados : "+ cont);
	}
	
	public static void main(String[] args) throws Exception {
		
		rr.setUsarThread(false);
		
		boolean migrarClientes = false;
		boolean migrarArticulos = false;
		boolean migrarCtaCte = false;
		
		DBPopulationMRA mra = new DBPopulationMRA();
		mra.dropTablas();
		
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader bfr = new BufferedReader(isr);
		
		System.out.print("Migrar Clientes ? (Y/N):");
		String in = bfr.readLine();
		if (in.compareTo("Y") == 0) {
			migrarClientes = true;
		}
		
		System.out.print("Migrar Movimientos Cta Cte ? (Y/N):");
		String in2 = bfr.readLine();
		if (in2.compareTo("Y") == 0) {
			migrarCtaCte = true;
		}
		
		System.out.print("Migrar Articulos ? (Y/N):");
		in = bfr.readLine();
		if (in.compareTo("Y") == 0) {
			migrarArticulos = true;
		}
		
		mra.poblarBD();		
		mra.cargarItemsDeGastos();
		
		if (migrarArticulos == true) {
			MRA_Articulos migArt = new MRA_Articulos();
			Deposito[] deps = {dep1S1, depSC};
			migArt.poblarArticulos(tt, deps);				
		}
		
		DBVistas.main(null); //Crear las vistas
		
		if (migrarClientes == true) {
			//ClienteMigraConfig.main(null);
			// rucs
			DBPopulationSET.main(null);
			// clientes y proveedores
			MRA_Clientes.main(null);
			// saldos de los clientes
			//dr MigracionMovimientosClientes.main(null);
			// saldos de los proveedores (pendiente)
		}
		
		if(migrarCtaCte == true){
			
			MigracionMovimientosClientes.main(null);
			MigracionMovimientosProveedores.main(null);
			
		}		
		rr.stop();
	}
}
