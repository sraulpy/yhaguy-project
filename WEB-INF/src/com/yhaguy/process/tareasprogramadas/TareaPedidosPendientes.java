package com.yhaguy.process.tareasprogramadas;

import java.util.Date;
import java.util.List;

import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Tarea_Programada;
import com.yhaguy.domain.Venta;

public class TareaPedidosPendientes {

	public static void main(String[] args) {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			List<Venta> pedidos = rr.getPedidosPendientesPreparacion();
			for (Venta venta : pedidos) {
				venta.setAuxi("");
				rr.saveObject(venta, venta.getUsuarioMod());
			}
			
			Tarea_Programada tarea = new Tarea_Programada();
			tarea.setFecha(new Date());
			tarea.setDescripcion("ALTA DE PEDIDOS PENDIENTES: " + pedidos.size());
			rr.saveObject(tarea, "sys");
			
			System.out.println("ALTA DE PEDIDOS PENDIENTES REALIZADO.." + pedidos.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
