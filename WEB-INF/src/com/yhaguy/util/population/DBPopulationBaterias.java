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
import com.yhaguy.util.migracion.MigracionPlanDeCuentas;
import com.yhaguy.util.migracion.baterias.BAT_Articulos;
import com.yhaguy.util.migracion.baterias.BAT_Clientes;

public class DBPopulationBaterias {

	/**
	 * Configuracion inicial de usuarios - funcionarios - accesos Bateria..
	 */
	
	private static RegisterDomain rr = RegisterDomain.getInstance();
	private static DBPopulationTipos tt;
	private static Deposito dep1;
	private static Deposito dep2;
	private static Deposito dep3;
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
		String iniFile = "./WEB-INF/menu_config_bat.ini";
		String iniUser = "./WEB-INF/usuarios-propiedad_bat.ini";
		UsuarioPerfilParser.loadMenuConfig(iniFile, iniUser);
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
		System.out
				.println("-------------------- Poblando Tipos... --------------------");
		tt = this.cargarTipos();
		MigracionPlanDeCuentas.main(null);

		System.out
				.println("-------------------- Poblando Perfiles... --------------------");
		this.cargarPerfiles();

		Usuario natalia = (Usuario) rr.getObject(Usuario.class.getName(),
				new Long(1));
		Usuario giselle = (Usuario) rr.getObject(Usuario.class.getName(),
				new Long(2));
		Usuario raquel = (Usuario) rr.getObject(Usuario.class.getName(),
				new Long(3));
		Usuario oscar = (Usuario) rr.getObject(Usuario.class.getName(),
				new Long(4));
		Usuario narciso = (Usuario) rr.getObject(Usuario.class.getName(),
				new Long(5));
		Usuario matias = (Usuario) rr.getObject(Usuario.class.getName(),
				new Long(6));
		Usuario ariel = (Usuario) rr.getObject(Usuario.class.getName(),
				new Long(7));
		Usuario luis = (Usuario) rr.getObject(Usuario.class.getName(),
				new Long(8));
		Usuario pablo = (Usuario) rr.getObject(Usuario.class.getName(),
				new Long(9));
		Usuario julio = (Usuario) rr.getObject(Usuario.class.getName(),
				new Long(10));
		Usuario miguel = (Usuario) rr.getObject(Usuario.class.getName(),
				new Long(11));
		Usuario juan = (Usuario) rr.getObject(Usuario.class.getName(),
				new Long(12));
		Usuario roque = (Usuario) rr.getObject(Usuario.class.getName(),
				new Long(13));

		System.out
				.println("------------------- Poblando Funcionarios... -------------------");

		/** Depósitos **/

		dep1 = new Deposito();
		dep1.setDescripcion("BAT-D1");
		dep1.setObservacion("Depósito 1 Baterías");
		grabarDB(dep1);

		dep2 = new Deposito();
		dep2.setDescripcion("BAT-D2");
		dep2.setObservacion("Depósito 2 Baterías");
		grabarDB(dep2);

		dep3 = new Deposito();
		dep3.setDescripcion("BAT-D3");
		dep3.setObservacion("Depósito 3 Baterías");
		grabarDB(dep3);

		Set<Deposito> deps = new HashSet<Deposito>();
		deps.add(dep1);
		deps.add(dep2);
		deps.add(dep3);

		SucursalApp central = new SucursalApp();
		central.setNombre(Configuracion.ID_SUCURSALAPP_BATERIAS);
		central.setDescripcion(Configuracion.ID_SUCURSALAPP_BATERIAS);
		central.setEstablecimiento("1");
		central.setDireccion("");
		central.setTelefono("");
		central.setDepositos(deps);
		grabarDB(central);

		Set<SucursalApp> sucs = new HashSet<SucursalApp>();
		sucs.add(central);

		/** Un centro de costo **/
		Set<CentroCosto> ccs = new HashSet<CentroCosto>();
		String key = Configuracion.NRO_CENTRO_COSTO;

		CentroCosto cc = new CentroCosto();
		cc.setNumero(key + "-" + AutoNumeroControl.getAutoNumero(key, 5));
		cc.setDescripcion("ADMINISTRACIÓN - BAT");
		cc.setMontoAsignado(100000000);
		ccs.add(cc);

		/** Un Departamento Administrativo **/
		List<CuentaContable> cuentas = rr.getCuentasContables();
		DepartamentoApp dto1 = new DepartamentoApp();
		dto1.setNombre("ADMINISTRACIÓN");
		dto1.setDescripcion("Departamento de Administración");
		dto1.setSucursal(central);
		dto1.setCentroCostos(ccs);
		dto1.setCuentas(new HashSet<CuentaContable>(cuentas));
		grabarDB(dto1);

		/** Tipos **/
		Tipo rubroFuncionario = rr
				.getTipoPorSigla(Configuracion.SIGLA_RUBRO_EMPRESA_FUNCIONARIO);
		Tipo monedaLocal = rr
				.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);
		Tipo paraguay = rr.getTipoPorSigla(Configuracion.SIGLA_PAIS_PARAGUAY);
		Tipo personaFisica = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_PERSONA_FISICA);
		Tipo auxiliarAdm = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_CARGO_AUXILIAR_ADMINISTRATIVO);
		Tipo gerenteAdmi = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_CARGO_GERENTE_ADMINISTRATIVO);
		Tipo estadoActivo = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_FUNCIONARIO_ESTADO_ACTIVO);
		Tipo encargadoDepo = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_CARGO_ENCARGADO_DEPOSITO);
		Tipo vendedorMostrador = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_CARGO_VENTAS_MOSTRADOR);

		Set<Tipo> rubros = new HashSet<Tipo>();
		rubros.add(rubroFuncionario);

		Set<Tipo> monedas = new HashSet<Tipo>();

		/***************** Configuración usuario Natalia Crespi ****************/

		Empresa empNatalia = new Empresa();
		empNatalia.setNombre("Crespi, Natalia");
		empNatalia.setCodigoEmpresa("0");
		empNatalia.setFechaIngreso(new Date());
		empNatalia.setObservacion("sin obs");
		empNatalia.setRazonSocial("Natalia Crespi");
		empNatalia.setRubroEmpresas(rubros);
		empNatalia.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empNatalia.setMoneda(monedaLocal);
		empNatalia.setMonedas(monedas);
		empNatalia.setPais(paraguay);
		empNatalia.setSigla("NDF");
		empNatalia.setTipoPersona(personaFisica);
		grabarDB(empNatalia);

		AccesoApp accNatalia = new AccesoApp();
		accNatalia.setDescripcion("Acceso Natalia Crespi");
		accNatalia.setDepartamento(dto1);
		accNatalia.setSucursales(sucs);
		accNatalia.setUsuario(natalia);
		grabarDB(accNatalia);

		Set<AccesoApp> accsNatalia = new HashSet<AccesoApp>();
		accsNatalia.add(accNatalia);

		Funcionario funNatalia = new Funcionario();
		funNatalia.setEmpresa(empNatalia);
		funNatalia.setFuncionarioCargo(gerenteAdmi);
		funNatalia.setFuncionarioEstado(estadoActivo);
		funNatalia.setAccesos(accsNatalia);
		grabarDB(funNatalia);

		/***********************************************************************/

		/*************** Configuración usuario Giselle Ortiz ***************/

		Empresa empGiselle = new Empresa();
		empGiselle.setNombre("Ortíz, Giselle");
		empGiselle.setCodigoEmpresa("0");
		empGiselle.setFechaIngreso(new Date());
		empGiselle.setObservacion("sin obs");
		empGiselle.setRazonSocial("Giselle Ortíz");
		empGiselle.setRubroEmpresas(rubros);
		empGiselle.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empGiselle.setMoneda(monedaLocal);
		empGiselle.setMonedas(monedas);
		empGiselle.setPais(paraguay);
		empGiselle.setSigla("NDF");
		empGiselle.setTipoPersona(personaFisica);
		grabarDB(empGiselle);

		AccesoApp accGiselle = new AccesoApp();
		accGiselle.setDescripcion("Acceso Giselle Ortíz");
		accGiselle.setDepartamento(dto1);
		accGiselle.setSucursales(sucs);
		accGiselle.setUsuario(giselle);
		grabarDB(accGiselle);

		Set<AccesoApp> accsGiselle = new HashSet<AccesoApp>();
		accsGiselle.add(accGiselle);

		Funcionario funGiselle = new Funcionario();
		funGiselle.setEmpresa(empGiselle);
		funGiselle.setFuncionarioCargo(auxiliarAdm);
		funGiselle.setFuncionarioEstado(estadoActivo);
		funGiselle.setAccesos(accsGiselle);
		grabarDB(funGiselle);

		/***********************************************************************/

		/************** Configuración usuario Raquel Torrasca **************/

		Empresa empRaquel = new Empresa();
		empRaquel.setNombre("Torrasca, Raquel");
		empRaquel.setCodigoEmpresa("0");
		empRaquel.setFechaIngreso(new Date());
		empRaquel.setObservacion("sin obs");
		empRaquel.setRazonSocial("Raquel Torrasca");
		empRaquel.setRubroEmpresas(rubros);
		empRaquel.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empRaquel.setMoneda(monedaLocal);
		empRaquel.setMonedas(monedas);
		empRaquel.setPais(paraguay);
		empRaquel.setSigla("NDF");
		empRaquel.setTipoPersona(personaFisica);
		grabarDB(empRaquel);

		AccesoApp accRaquel = new AccesoApp();
		accRaquel.setDescripcion("Acceso Raquel Torrasca");
		accRaquel.setDepartamento(dto1);
		accRaquel.setSucursales(sucs);
		accRaquel.setUsuario(raquel);
		grabarDB(accRaquel);

		Set<AccesoApp> accsRaquel = new HashSet<AccesoApp>();
		accsRaquel.add(accRaquel);

		Funcionario funRaquel = new Funcionario();
		funRaquel.setEmpresa(empRaquel);
		funRaquel.setFuncionarioCargo(auxiliarAdm);
		funRaquel.setFuncionarioEstado(estadoActivo);
		funRaquel.setAccesos(accsRaquel);
		grabarDB(funRaquel);

		/***************** Configuración usuario Oscar Vera ****************/

		Empresa empOscar = new Empresa();
		empOscar.setNombre("Vera, Oscar");
		empOscar.setCodigoEmpresa("0");
		empOscar.setFechaIngreso(new Date());
		empOscar.setObservacion("sin obs");
		empOscar.setRazonSocial("Oscar Vera");
		empOscar.setRubroEmpresas(rubros);
		empOscar.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empOscar.setMoneda(monedaLocal);
		empOscar.setMonedas(monedas);
		empOscar.setPais(paraguay);
		empOscar.setSigla("NDF");
		empOscar.setTipoPersona(personaFisica);
		grabarDB(empOscar);

		AccesoApp accOscar = new AccesoApp();
		accOscar.setDescripcion("Acceso Usuario Oscar");
		accOscar.setDepartamento(dto1);
		accOscar.setSucursales(sucs);
		accOscar.setUsuario(oscar);
		grabarDB(accOscar);

		Set<AccesoApp> accsOscar = new HashSet<AccesoApp>();
		accsOscar.add(accOscar);

		Funcionario funOscar = new Funcionario();
		funOscar.setEmpresa(empOscar);
		funOscar.setFuncionarioCargo(auxiliarAdm);
		funOscar.setFuncionarioEstado(estadoActivo);
		funOscar.setAccesos(accsOscar);
		grabarDB(funOscar);

		/*************** Configuración usuario Narciso Montiel ***************/

		Empresa empNarciso = new Empresa();
		empNarciso.setNombre("Montiel, Narciso");
		empNarciso.setCodigoEmpresa("0");
		empNarciso.setFechaIngreso(new Date());
		empNarciso.setObservacion("sin obs");
		empNarciso.setRazonSocial("Narciso Montiel");
		empNarciso.setRubroEmpresas(rubros);
		empNarciso.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empNarciso.setMoneda(monedaLocal);
		empNarciso.setMonedas(monedas);
		empNarciso.setPais(paraguay);
		empNarciso.setSigla("NDF");
		empNarciso.setTipoPersona(personaFisica);
		grabarDB(empNarciso);

		AccesoApp accNarciso = new AccesoApp();
		accNarciso.setDescripcion("Narciso Montiel");
		accNarciso.setDepartamento(dto1);
		accNarciso.setSucursales(sucs);
		accNarciso.setUsuario(narciso);
		grabarDB(accNarciso);

		Set<AccesoApp> accsNarciso = new HashSet<AccesoApp>();
		accsNarciso.add(accNarciso);

		Funcionario funNarciso = new Funcionario();
		funNarciso.setEmpresa(empNarciso);
		funNarciso.setFuncionarioCargo(encargadoDepo);
		funNarciso.setFuncionarioEstado(estadoActivo);
		funNarciso.setAccesos(accsNarciso);
		grabarDB(funNarciso);

		/*************** Configuración usuario Matias Figueredo *********************/

		Empresa empMatias = new Empresa();
		empMatias.setNombre("Matias, Figueredo");
		empMatias.setCodigoEmpresa("0");
		empMatias.setFechaIngreso(new Date());
		empMatias.setObservacion("sin obs");
		empMatias.setRazonSocial("Matias Figueredo");
		empMatias.setRubroEmpresas(rubros);
		empMatias.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empMatias.setMoneda(monedaLocal);
		empMatias.setMonedas(monedas);
		empMatias.setPais(paraguay);
		empMatias.setSigla("NDF");
		empMatias.setTipoPersona(personaFisica);
		grabarDB(empMatias);

		AccesoApp accMatias = new AccesoApp();
		accMatias.setDescripcion("Matias Figueredo");
		accMatias.setDepartamento(dto1);
		accMatias.setSucursales(sucs);
		accMatias.setUsuario(matias);
		grabarDB(accMatias);

		Set<AccesoApp> accsMatias = new HashSet<AccesoApp>();
		accsMatias.add(accMatias);

		Funcionario funMatias = new Funcionario();
		funMatias.setEmpresa(empMatias);
		funMatias.setFuncionarioCargo(encargadoDepo);
		funMatias.setFuncionarioEstado(estadoActivo);
		funMatias.setAccesos(accsMatias);
		grabarDB(funMatias);

		/********************** Configuración usuario Ariel Benitez **********************/

		Empresa empAriel = new Empresa();
		empAriel.setNombre("Ariel, Benítez");
		empAriel.setCodigoEmpresa("0");
		empAriel.setFechaIngreso(new Date());
		empAriel.setObservacion("sin obs");
		empAriel.setRazonSocial("Ariel Benítez");
		empAriel.setRubroEmpresas(rubros);
		empAriel.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empAriel.setMoneda(monedaLocal);
		empAriel.setMonedas(monedas);
		empAriel.setPais(paraguay);
		empAriel.setSigla("NDF");
		empAriel.setTipoPersona(personaFisica);
		grabarDB(empAriel);

		AccesoApp accAriel = new AccesoApp();
		accAriel.setDescripcion("Acceso Ariel Benítez");
		accAriel.setDepartamento(dto1);
		accAriel.setSucursales(sucs);
		accAriel.setUsuario(ariel);
		grabarDB(accAriel);

		Set<AccesoApp> accsWilfrido = new HashSet<AccesoApp>();
		accsWilfrido.add(accAriel);

		Funcionario funAriel = new Funcionario();
		funAriel.setEmpresa(empAriel);
		funAriel.setFuncionarioCargo(auxiliarAdm);
		funAriel.setFuncionarioEstado(estadoActivo);
		funAriel.setAccesos(accsWilfrido);
		grabarDB(funAriel);

		/********************** Configuración usuario Luis Benitez **********************/

		Empresa empLuis = new Empresa();
		empLuis.setNombre("Benítez, Luis");
		empLuis.setCodigoEmpresa("0");
		empLuis.setFechaIngreso(new Date());
		empLuis.setObservacion("sin obs");
		empLuis.setRazonSocial("Luis Benítez");
		empLuis.setRubroEmpresas(rubros);
		empLuis.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empLuis.setMoneda(monedaLocal);
		empLuis.setMonedas(monedas);
		empLuis.setPais(paraguay);
		empLuis.setSigla("NDF");
		empLuis.setTipoPersona(personaFisica);
		grabarDB(empLuis);

		AccesoApp accLuis = new AccesoApp();
		accLuis.setDescripcion("Acceso Luis Benitez");
		accLuis.setDepartamento(dto1);
		accLuis.setSucursales(sucs);
		accLuis.setUsuario(luis);
		grabarDB(accLuis);

		Set<AccesoApp> accsLuis = new HashSet<AccesoApp>();
		accsLuis.add(accLuis);

		Funcionario funLuis = new Funcionario();
		funLuis.setEmpresa(empLuis);
		funLuis.setFuncionarioCargo(vendedorMostrador);
		funLuis.setFuncionarioEstado(estadoActivo);
		funLuis.setAccesos(accsLuis);
		grabarDB(funLuis);

		/********************** Configuración usuario Pablo Jara **********************/

		Empresa empPablo = new Empresa();
		empPablo.setNombre("Jara, Pablo");
		empPablo.setCodigoEmpresa("0");
		empPablo.setFechaIngreso(new Date());
		empPablo.setObservacion("sin obs");
		empPablo.setRazonSocial("Pablo Jara");
		empPablo.setRubroEmpresas(rubros);
		empPablo.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empPablo.setMoneda(monedaLocal);
		empPablo.setMonedas(monedas);
		empPablo.setPais(paraguay);
		empPablo.setSigla("NDF");
		empPablo.setTipoPersona(personaFisica);
		grabarDB(empPablo);

		AccesoApp accPablo = new AccesoApp();
		accPablo.setDescripcion("Acceso Pablo Jara");
		accPablo.setDepartamento(dto1);
		accPablo.setSucursales(sucs);
		accPablo.setUsuario(pablo);
		grabarDB(accPablo);

		Set<AccesoApp> accsPablo = new HashSet<AccesoApp>();
		accsPablo.add(accPablo);

		Funcionario funPablo = new Funcionario();
		funPablo.setEmpresa(empPablo);
		funPablo.setFuncionarioCargo(vendedorMostrador);
		funPablo.setFuncionarioEstado(estadoActivo);
		funPablo.setAccesos(accsPablo);
		grabarDB(funPablo);

		/********************** Configuración usuario Julio Duarte **********************/

		Empresa empJulio = new Empresa();
		empJulio.setNombre("Julio, Duarte");
		empJulio.setCodigoEmpresa("0");
		empJulio.setFechaIngreso(new Date());
		empJulio.setObservacion("sin obs");
		empJulio.setRazonSocial("Julio Duarte");
		empJulio.setRubroEmpresas(rubros);
		empJulio.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empJulio.setMoneda(monedaLocal);
		empJulio.setMonedas(monedas);
		empJulio.setPais(paraguay);
		empJulio.setSigla("NDF");
		empJulio.setTipoPersona(personaFisica);
		grabarDB(empJulio);

		AccesoApp accJulio = new AccesoApp();
		accJulio.setDescripcion("Julio Duarte");
		accJulio.setDepartamento(dto1);
		accJulio.setSucursales(sucs);
		accJulio.setUsuario(julio);
		grabarDB(accJulio);

		Set<AccesoApp> accsJulio = new HashSet<AccesoApp>();
		accsJulio.add(accJulio);

		Funcionario funJulio = new Funcionario();
		funJulio.setEmpresa(empJulio);
		funJulio.setFuncionarioCargo(vendedorMostrador);
		funJulio.setFuncionarioEstado(estadoActivo);
		funJulio.setAccesos(accsJulio);
		grabarDB(funJulio);

		/********************** Configuración usuario Miguel Otazu **********************/

		Empresa empMiguel = new Empresa();
		empMiguel.setNombre("Otazú, Miguel");
		empMiguel.setCodigoEmpresa("0");
		empMiguel.setFechaIngreso(new Date());
		empMiguel.setObservacion("sin obs");
		empMiguel.setRazonSocial("Miguel Otazú");
		empMiguel.setRubroEmpresas(rubros);
		empMiguel.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empMiguel.setMoneda(monedaLocal);
		empMiguel.setMonedas(monedas);
		empMiguel.setPais(paraguay);
		empMiguel.setSigla("NDF");
		empMiguel.setTipoPersona(personaFisica);
		grabarDB(empMiguel);

		AccesoApp accMiguel = new AccesoApp();
		accMiguel.setDescripcion("Miguel Otazu");
		accMiguel.setDepartamento(dto1);
		accMiguel.setSucursales(sucs);
		accMiguel.setUsuario(miguel);
		grabarDB(accMiguel);

		Set<AccesoApp> accsMiguel = new HashSet<AccesoApp>();
		accsMiguel.add(accMiguel);

		Funcionario funMiguel = new Funcionario();
		funMiguel.setEmpresa(empMiguel);
		funMiguel.setFuncionarioCargo(vendedorMostrador);
		funMiguel.setFuncionarioEstado(estadoActivo);
		funMiguel.setAccesos(accsMiguel);
		grabarDB(funMiguel);

		/********************** Configuración usuario Juan Ibarra **********************/

		Empresa empJuan = new Empresa();
		empJuan.setNombre("Ibarra, Juan");
		empJuan.setCodigoEmpresa("0");
		empJuan.setFechaIngreso(new Date());
		empJuan.setObservacion("sin obs");
		empJuan.setRazonSocial("Juan Ibarra");
		empJuan.setRubroEmpresas(rubros);
		empJuan.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empJuan.setMoneda(monedaLocal);
		empJuan.setMonedas(monedas);
		empJuan.setPais(paraguay);
		empJuan.setSigla("NDF");
		empJuan.setTipoPersona(personaFisica);
		grabarDB(empJuan);

		AccesoApp accJuan = new AccesoApp();
		accJuan.setDescripcion("Juan Ibarra");
		accJuan.setDepartamento(dto1);
		accJuan.setSucursales(sucs);
		accJuan.setUsuario(juan);
		grabarDB(accJuan);

		Set<AccesoApp> accsJuan = new HashSet<AccesoApp>();
		accsJuan.add(accJuan);

		Funcionario funJuan = new Funcionario();
		funJuan.setEmpresa(empJuan);
		funJuan.setFuncionarioCargo(vendedorMostrador);
		funJuan.setFuncionarioEstado(estadoActivo);
		funJuan.setAccesos(accsJuan);
		grabarDB(funJuan);

		/********************** Configuración usuario Roque Amarilla **********************/

		Empresa empRoque = new Empresa();
		empRoque.setNombre("Amarilla, Roque");
		empRoque.setCodigoEmpresa("0");
		empRoque.setFechaIngreso(new Date());
		empRoque.setObservacion("sin obs");
		empRoque.setRazonSocial("Roque Amarilla");
		empRoque.setRubroEmpresas(rubros);
		empRoque.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empRoque.setMoneda(monedaLocal);
		empRoque.setMonedas(monedas);
		empRoque.setPais(paraguay);
		empRoque.setSigla("NDF");
		empRoque.setTipoPersona(personaFisica);
		grabarDB(empRoque);

		AccesoApp accRoque = new AccesoApp();
		accRoque.setDescripcion("Roque Amarilla");
		accRoque.setDepartamento(dto1);
		accRoque.setSucursales(sucs);
		accRoque.setUsuario(roque);
		grabarDB(accRoque);

		Set<AccesoApp> accsRoque = new HashSet<AccesoApp>();
		accsRoque.add(accRoque);

		Funcionario funRoque = new Funcionario();
		funRoque.setEmpresa(empRoque);
		funRoque.setFuncionarioCargo(vendedorMostrador);
		funRoque.setFuncionarioEstado(estadoActivo);
		funRoque.setAccesos(accsRoque);
		grabarDB(funRoque);

		/************************* Clientes Ocasionales ************************/

		String siglaClienteActivo = Configuracion.SIGLA_ESTADO_CLIENTE;
		String siglaCatCliAldia = Configuracion.SIGLA_CATEGORIA_CLIENTE;
		String aliasCtCliOcasional = Configuracion.ALIAS_CUENTA_CLIENTES_OCASIONALES;
		String codigoCliOcasional = Configuracion.CODIGO_CLIENTE_OCASIONAL_CL;
		String rucEmpresaLocal = Configuracion.RUC_EMPRESA_LOCAL;

		Tipo estadoClienteActivo = rr.getTipoPorSigla_Index(siglaClienteActivo,	1);
		Tipo categoriaClienteAlDia = rr.getTipoPorSigla_Index(siglaCatCliAldia,	1);
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
		 * Los datos correspondientes a las lineas de creditos utilizadas en
		 * MRA.
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

		CtaCteLineaCredito l11 = new CtaCteLineaCredito();
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
		 * El tipo de cambio del Guarani que siempre es igual a '1' tanto para
		 * App y para BCP
		 */
		TipoCambio tc1 = new TipoCambio();
		tc1.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		tc1.setTipoCambio(rr
				.getTipoPorDescripcion(Configuracion.ID_TIPO_CAMBIO_APP));
		tc1.setFecha(new Date());
		tc1.setCompra(1);
		tc1.setVenta(1);
		grabarDB(tc1);

		TipoCambio tc2 = new TipoCambio();
		tc2.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		tc2.setTipoCambio(rr
				.getTipoPorDescripcion(Configuracion.ID_TIPO_CAMBIO_BCP));
		tc2.setFecha(new Date());
		tc2.setCompra(1);
		tc2.setVenta(1);
		grabarDB(tc2);

		/**
		 * Tipo de Cambio para moneda Dolar..
		 */
		TipoCambio tc3 = new TipoCambio();
		tc3.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_DOLAR));
		tc3.setTipoCambio(rr
				.getTipoPorDescripcion(Configuracion.ID_TIPO_CAMBIO_BCP));
		tc3.setFecha(new Date());
		tc3.setCompra(5100);
		tc3.setVenta(5100);
		grabarDB(tc3);

		/*************************** Banco **********************************/
		Banco bc01 = new Banco();
		bc01.setDescripcion("Continental");
		bc01.setBancoTipo(rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_BANCOS_TERCEROS_CONTI));
		grabarDB(bc01);

		Banco bc02 = new Banco();
		bc02.setDescripcion("Itaú");
		bc02.setBancoTipo(rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_BANCOS_TERCEROS_ITAU));
		grabarDB(bc02);

		BancoCta bcc01 = new BancoCta();
		bcc01.setBanco(bc01);
		bcc01.setFechaApertura(new Date());
		bcc01.setMoneda(tt.guarani);
		bcc01.setNroCuenta("06.485.580-08");
		bcc01.setTipo(tt.bancoCtaCte);
		bcc01.setCuentaContable(rr
				.getCuentaContableByAlias_("CT-BANCO-CONTINENTAL-BAT"));

		grabarDB(bcc01);

		BancoCta bcc02 = new BancoCta();
		bcc02.setBanco(bc02);
		bcc02.setFechaApertura(new Date());
		bcc02.setMoneda(tt.guarani);
		bcc02.setNroCuenta("80027742-2");
		bcc02.setTipo(tt.bancoCtaCte);
		bcc02.setCuentaContable(rr
				.getCuentaContableByAlias_("CT-BANCO-ITAU-BAT"));
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
			pr.setSucursal(central);
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
		
		ArticuloListaPrecio distribuidor = new ArticuloListaPrecio();
		distribuidor.setDescripcion("DISTRIBUIDOR");
		distribuidor.setMargen(40);
		distribuidor.setActivo(true);
		grabarDB(distribuidor);

		/**************************** Vehículos ***************************/
		this.poblarVEH(central);

		/**************************** Ubicaciones ***************************/
		this.poblarAU();

		// Reportes
		this.poblarDefinicionReportes();
	}	

	/**
	 * Poblar vehículos BAT
	 */
	public void poblarVEH(SucursalApp suc) throws Exception {

		System.out.println("-------------------- Poblando Vehículos... --------------------");

		Vehiculo veh1 = new Vehiculo();
		veh1.setCodigo(AutoNumeroControl.getAutoNumeroKey(KEY_NRO, 4));
		veh1.setMarca("KENTON/Moto");
		veh1.setModelo("GL/25/2014");
		veh1.setChapa("566 BGU");
		veh1.setColor("Rojo");
		veh1.setSucursal(suc);
		grabarDB(veh1);

		Vehiculo veh2 = new Vehiculo();
		veh2.setCodigo(AutoNumeroControl.getAutoNumeroKey(KEY_NRO, 4));
		veh2.setMarca("FIAT");
		veh2.setModelo("2012-Fioreno");
		veh2.setChapa("B4P 937");
		veh2.setColor("Blanco");
		veh2.setSucursal(suc);
		grabarDB(veh2);

		Vehiculo veh3 = new Vehiculo();
		veh3.setCodigo(AutoNumeroControl.getAutoNumeroKey(KEY_NRO, 4));
		veh3.setMarca("CHANGAN");
		veh3.setModelo("SC1024GDD41/2014");
		veh3.setChapa("OBO348");
		veh3.setColor("Blanco");
		veh3.setSucursal(suc);
		grabarDB(veh3);

	}


/**
 * Poblar Articulo Ubicaciones BAT
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
				"Pagos por Cliente y Fecha de Pago",
				"Listado de Retenciones por fecha",
				"Listado de Saldos de Clientes",
				"Listado de Saldos de Clientes detallado",
				"Listado de Clientes sin Movimiento",
				"Verificación de Pagos con Cheques",
				"Detalles de Pagos del Periodo", "Pagos por Número de Recibo",
				"Estado de Cuenta de Proveedores",
				"Estado de Cuenta de Clientes", "Pagos por Proveedor",
				"Cheques a vencer", "Cheques por Cliente",
				"Cheques por Número de comprobante", "Cheques descontados" };

		String[] compras = new String[] { "Listado de Compras" };

		String[] ventas = new String[] {
				"Listado de Ventas y Notas de Crédito genérico",
				"Ventas y Utilidad",
				"Listado de Ventas por código de operación",
				"Ventas por Vendedor", "Ventas y cobranzas por Vendedor" };

		String[] stock = new String[] { "Articulos genérico" };

		String[] logistica = new String[] { "Listado de Repartos" };

		String[] contabilidad = new String[] { "Libro Compras", "Libro Ventas",
				"Ventas Hechauka", "Retenciones Hechauka" };

		String[] rrhh = new String[] { "Liquidación de vendedores por ventas" };

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

	/**
	 * Para tener el historial de migracion en una tabla..
	 */
	public void poblarArticuloHistorialMigracion() throws Exception {

		String src = "./WEB-INF/docs/MigracionBaterias/Articulos/articulos.csv";

		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CODIGO", CSV.STRING },
				{ "DESCRIPCION", CSV.STRING }, { "COSTO", CSV.NUMERICO },
				{ "PRECIO", CSV.NUMERICO }, { "STOCK", CSV.NUMERICO } };

		System.out
				.println("---------------------- Poblando Articulos ----------------------");

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
			int stock = ((Float) csv.getDetalle("STOCK")).intValue();

			System.out.println(codigo + " - " + descripcion + " - " + costo
					+ " - " + precio + " - " + stock);
			Articulo art = rr.getArticulo(codigo);

			if (art != null) {
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
				artMra.setSucursal(rr.getSucursalAppById(1));
				rr.saveObject(artMra, "Migracion");
			} else {
				cont++;
			}
		}
		System.out.println("------------- Art no encontrados : " + cont);
	}
	
	public static void main(String[] args) throws Exception {
		
		rr.setUsarThread(false);
		
		boolean migrarClientes = false;
		boolean migrarArticulos = false;
		
		DBPopulationBaterias bat = new DBPopulationBaterias();
		bat.dropTablas();
		
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader bfr = new BufferedReader(isr);
		
		System.out.print("Migrar Clientes ? (Y/N):");
		String in = bfr.readLine();
		if (in.compareTo("Y") == 0) {
			migrarClientes = true;
		}
		
		System.out.print("Migrar Articulos ? (Y/N):");
		in = bfr.readLine();
		if (in.compareTo("Y") == 0) {
			migrarArticulos = true;
		}
		
		bat.poblarBD();		
		bat.cargarItemsDeGastos();
		
		if (migrarArticulos == true) {
			BAT_Articulos migArt = new BAT_Articulos();
			Deposito[] deps = {dep1, dep2, dep3};
			migArt.poblarArticulos(tt, deps);				
		}
		
		DBVistas.main(null); //Crear las vistas
		
		if (migrarClientes == true) {
			DBPopulationSET.main(null);
			BAT_Clientes migCli = new BAT_Clientes();
			migCli.poblarEmpresas();			
		}	
		rr.stop();
	}
}
