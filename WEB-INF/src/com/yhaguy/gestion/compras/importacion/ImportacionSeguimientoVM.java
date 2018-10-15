package com.yhaguy.gestion.compras.importacion;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Init;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.ImportacionPedidoCompra;
import com.yhaguy.domain.RegisterDomain;

public class ImportacionSeguimientoVM extends SimpleViewModel {
	
	private List<ImportacionPedidoCompra> imps1 = new ArrayList<ImportacionPedidoCompra>();
	private List<ImportacionPedidoCompra> imps2 = new ArrayList<ImportacionPedidoCompra>();
	private List<ImportacionPedidoCompra> imps3 = new ArrayList<ImportacionPedidoCompra>();

	@Init(superclass = true)
	public void init() {
		try {
			this.distribuirImportaciones();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	/**
	 * distribuir pedidos en 4 listas..
	 */
	private void distribuirImportaciones() throws Exception {
		this.imps1 = new ArrayList<ImportacionPedidoCompra>();
		this.imps2 = new ArrayList<ImportacionPedidoCompra>();
		this.imps3 = new ArrayList<ImportacionPedidoCompra>();
		int index = 1;		
		for (ImportacionPedidoCompra imp : this.getImportacionesPendientes()) {
			if (index == 1) {
				this.imps1.add(imp);
				index++;
			} else {
				if (index == 2) {
					this.imps2.add(imp);
					index++;
				} else {
					if (index == 3) {
						this.imps3.add(imp);
						index = 1;
					}
				} 
			}
		}
	}
	
	/**
	 * GETS / SETS
	 */
	
	/**
	 * @return las importaciones pendientes..
	 */
	public List<ImportacionPedidoCompra> getImportacionesPendientes() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getImportacionesPendientes();
	}

	public List<ImportacionPedidoCompra> getImps1() {
		return imps1;
	}

	public void setImps1(List<ImportacionPedidoCompra> imps1) {
		this.imps1 = imps1;
	}

	public List<ImportacionPedidoCompra> getImps2() {
		return imps2;
	}

	public void setImps2(List<ImportacionPedidoCompra> imps2) {
		this.imps2 = imps2;
	}

	public List<ImportacionPedidoCompra> getImps3() {
		return imps3;
	}

	public void setImps3(List<ImportacionPedidoCompra> imps3) {
		this.imps3 = imps3;
	}
}
