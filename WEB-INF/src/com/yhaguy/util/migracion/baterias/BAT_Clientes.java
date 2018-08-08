package com.yhaguy.util.migracion.baterias;

import com.coreweb.extras.csv.CSV;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Sucursal;

public class BAT_Clientes {
	
	static RegisterDomain rr = RegisterDomain.getInstance();
	
	static String src = "./WEB-INF/docs/MigracionBaterias/clientes-proveedores/clientes-proveedores.csv";
	static String srcDir = "./WEB-INF/docs/MigracionBaterias/clientes-proveedores/direcciones.csv";
	static String srcBloq = "./WEB-INF/docs/MigracionBaterias/clientes-proveedores/cuentas-bloqueadas.csv";
	static String srcDup = "./WEB-INF/docs/MigracionBaterias/clientes-proveedores/cliente-duplicado.csv";
	
	String[][] cab = { { "Empresa", CSV.STRING } };
	String[][] det = { { "CODIGO", CSV.STRING }, { "RAZONSOCIAL", CSV.STRING },
			{ "DOCUMENTO", CSV.STRING }, { "RUC", CSV.STRING },
			{ "TIPOPERSONA", CSV.STRING }};
	
	String[][] detDir = { { "CODIGO", CSV.STRING }, { "DIRECCION", CSV.STRING },
			{ "TELEFONO", CSV.STRING }};
	
	String[][] detBloq = { { "CODIGO", CSV.STRING }};

	String[][] detDup = { { "CODIGO", CSV.STRING }, { "RAZONSOCIAL", CSV.STRING },
			{ "RUC", CSV.STRING }, { "DIRECCION", CSV.STRING },
			{ "TELEFONO", CSV.STRING }};
	
	/**
	 * pobla las empresas..
	 */
	public void poblarEmpresas() throws Exception {

		System.out.println("---------------------- Poblando Empresas ----------------------");

		CSV csv = new CSV(cab, det, src);

		csv.start();
		while (csv.hashNext()) {

			String codigo = csv.getDetalleString("CODIGO");
			String razonSocial = csv.getDetalleString("RAZONSOCIAL");
			String documento = csv.getDetalleString("DOCUMENTO");
			String ruc = csv.getDetalleString("RUC");
			String tipo = csv.getDetalleString("TIPOPERSONA");
			
			boolean exist = false;
			
			Empresa emp = rr.getEmpresaByRuc(ruc);
			
			if (emp == null) {
				emp = new Empresa();
			} else {
				exist = true;
			}
			
			emp.setCodigoEmpresa(codigo);
			emp.setCi(documento);
			emp.setRuc(ruc);
			emp.setRazonSocial(razonSocial);
			emp.setNombre(razonSocial);
			
			rr.saveObject(emp, "migracion");
			
			
			if (tipo.equals("1")) {				
				Cliente cliente = (Cliente) rr.getClienteByEmpresa(emp.getId());
				if (cliente == null) {
					cliente = new Cliente();
				}
				cliente.setEmpresa(emp);
				rr.saveObject(cliente, "migracion");
			} else {
				Proveedor prov = (Proveedor) rr.getProveedorByEmpresa(emp.getId());
				if (prov == null) {
					prov = new Proveedor();
				}
				prov.setEmpresa(emp);
				rr.saveObject(prov, "migracion");
			}
			System.out.println((exist == false? "NUEVO" : "ENCONTRADO") + " - " + codigo + " - " + razonSocial );
		}

		System.out.println("---------------------- Fin Empresas ----------------------");
	}
	
	/**
	 * poblar direcciones..
	 */
	public void setDirecciones() throws Exception {

		System.out.println("---------------------- Poblando direcciones ----------------------");

		CSV csv = new CSV(cab, detDir, srcDir);

		csv.start();
		while (csv.hashNext()) {

			String codigo = csv.getDetalleString("CODIGO");
			String direccion = csv.getDetalleString("DIRECCION");
			String telefono = csv.getDetalleString("TELEFONO");			
			
			Empresa emp = (Empresa) rr.getEmpresaByCodigoCliente(codigo);
			
			if (emp == null) {
				emp = (Empresa) rr.getEmpresaByCodigoProveedor(codigo);
			}
			
			if (emp != null) {
				if (emp.getSucursales().size() > 0) {
					for (Sucursal suc : emp.getSucursales()) {
						suc.setDireccion(direccion);
						suc.setTelefono(telefono);
						rr.saveObject(suc, "migracion");
						System.out.println("ACTUALIZADO " + emp.getRazonSocial());
					}
				} else {
					Sucursal suc = new Sucursal();
					suc.setNombre("Sucursal 1");
					suc.setDireccion(direccion);
					suc.setTelefono(telefono);
					emp.getSucursales().add(suc);
					rr.saveObject(emp, "migracion");
					System.out.println("AGREGADO " + emp.getRazonSocial());
				}
			}
		}

		System.out.println("---------------------- Fin Empresas ----------------------");
	}
	
	/**
	 * bloqueo de cuentas..
	 */
	public void bloqueoCuentas() throws Exception {

		System.out.println("---------------------- Bloqueando Cuentas ----------------------");

		CSV csv = new CSV(cab, detBloq, srcBloq);

		csv.start();
		while (csv.hashNext()) {

			String codigo = csv.getDetalleString("CODIGO");	
			
			Empresa emp = (Empresa) rr.getEmpresaByCodigoCliente(codigo);
			
			if (emp != null) {
				emp.setCuentaBloqueada(true);
				rr.saveObject(emp, "migracion");
				System.out.println("bloqueado - " + emp.getRazonSocial());
			}
		}

		System.out.println("---------------------- Fin Bloqueo Cuentas ----------------------");
	}
	
	/**
	 * add cliente duplicado..
	 */
	public void addClienteDuplicado() throws Exception {

		System.out.println("---------------------- Poblando cliente duplicado ----------------------");

		CSV csv = new CSV(cab, detDup, srcDup);

		csv.start();
		while (csv.hashNext()) {

			String codigo = csv.getDetalleString("CODIGO");
			String razonSocial = csv.getDetalleString("RAZONSOCIAL");
			String ruc = csv.getDetalleString("RUC");
			String direccion = csv.getDetalleString("DIRECCION");
			String telefono = csv.getDetalleString("TELEFONO");			
			
			Empresa emp = (Empresa) rr.getEmpresaByCodigoCliente(codigo);
			
			if (emp == null) {
				emp = new Empresa();
				emp.setCodigoEmpresa(codigo);
				emp.setRuc(ruc);
				emp.setRazonSocial(razonSocial);
				emp.setNombre(razonSocial);
				
				Sucursal suc = new Sucursal();
				suc.setNombre("Sucursal 1");
				suc.setDireccion(direccion);
				suc.setTelefono(telefono);
				emp.getSucursales().add(suc);
				rr.saveObject(emp, "migracion");
				System.out.println("AGREGADO " + emp.getRazonSocial());
			
				Cliente cli = new Cliente();
				cli.setEmpresa(emp);
				rr.saveObject(cli, "sys");
			}
		}

		System.out.println("---------------------- Fin Empresas ----------------------");
	}
	
	public static void main(String[] args) {
		BAT_Clientes test = new BAT_Clientes();
		try {
			//test.poblarEmpresas();
			//test.setDirecciones();
			test.bloqueoCuentas();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
