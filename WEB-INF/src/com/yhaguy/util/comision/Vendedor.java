package com.yhaguy.util.comision;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

abstract public class Vendedor {
	
	private static int PRE_FILE = 0;
	
	private double totalVenta = 0;
	private double totalContado = 0;
	private double totalCredito = 0;
	
	private String id = "id-e";
	private String usuario = "otro-e";
	private String nombre = "nombre externo";


	private ArrayList<Venta> ventas = new ArrayList<Venta>();
	private Hashtable<String, Venta> tVentas = new Hashtable<String, Venta>();

	private ArrayList<Venta> cobranzas = new ArrayList<Venta>();
	private Hashtable<String, Venta> tCobranzas = new Hashtable<String, Venta>();

	private ArrayList<Venta> ventasMarcaAsignada = new ArrayList<Venta>();
	private ArrayList<Venta> ventasMarcaNOAsignada = new ArrayList<Venta>();
	
	
	private Hashtable<String, ResumenMarca> resumenMarca = new Hashtable<String, ResumenMarca>();
	private Hashtable<String, ResumenProveedor> resumenProveedor = new Hashtable<String, ResumenProveedor>();
	
	public String getNombreArchivo() {
				
		//String out = this.id+"_"+this.getClass().getSimpleName()+"-" + this.nombre + " " + this.id + " "+ this.usuario + ".txt";
		String out = this.getIdFormato()+"_"+this.getClass().getSimpleName()+"-" + this.nombre + " "+ this.usuario + ".txt";
		return out;
	}
	
	public void initResumenMarca(){
		
	}

	public void initResumenProveedor(){
		this.resumenProveedor = new Hashtable<String, ResumenProveedor>();
	}

	public String toString(){
		String out = "";
		out = this.getId() + " " + this.usuario + " " +this.nombre;
		return out;
	}

	
	public void addVentaProveedorCobranza(Venta v){
		// Proveedor en cobranzas
		String idProveedor = v.getIdProveedor();
		ResumenProveedor rm = null;
		
		rm = this.resumenProveedor.get(idProveedor);
		if (rm == null){
			rm = new ResumenProveedor();
			rm.setIdProveedor(idProveedor);
			rm.setNombreProveedor(v.getProveedor());
			this.resumenProveedor.put(idProveedor, rm);
		}
		rm.addVenta(v);
	}

	public void addVentaMarcaVenta(Venta v){
		// Marca  en ventas, para las metas de los Crespi
		String idMarca = v.getIdMarca();
		
		String marcaGrupo = ConsCrespi.getMarcaGrupo(idMarca, this.id);
		
		ResumenMarca rma = null;
		
		rma = this.resumenMarca.get(marcaGrupo);
		if (rma == null){
			rma = new ResumenMarca();
			rma.setIdMarca(marcaGrupo);
			// lo busca con el ID rma.setNombreMarca(v.getMarca());
			this.resumenMarca.put(marcaGrupo, rma);
		}
		
		
		System.out.println("---------add:"+this.id+"  - "+v.getImporte() +" - "+ marcaGrupo);
		rma.addVenta(v);
	}
	
	public double getPorcentajeMarca(String idMarca, boolean esContado){
		String marcaGrupo = ConsCrespi.getMarcaGrupo(idMarca, this.id);
		ResumenMarca rma = this.resumenMarca.get(marcaGrupo);
		
		double importe = 0;
		
		if (esContado == true){
			importe = rma.getContadoTotal();
		}else{
			importe = rma.getCreditoTotal();
		}
		
		double por = ConsCrespi.getPorcentaje(this.getId(), idMarca, importe, esContado);
		return por;
	}
	
	
	
	
	public String getTextoResumenProveedors(){
		String out = "";
		out += "------- RESUMEN DE LOS PROVEEDORES ---------------\n";
		out += "        Proveedor        ;           Contado;   Comision ;  Por  ;    Contado Comp ;   Comision ;  Por  ;          Credito;   Comision ;  Por  ;     Credito Comp;    Comision;  Por  ;            Total;\n";
		ArrayList myArrayList=new ArrayList(this.resumenProveedor.values());
		Collections.sort(myArrayList, new ResumenProveedor());
		Iterator ite = myArrayList.iterator();
		while (ite.hasNext()){
			ResumenProveedor rm = (ResumenProveedor)ite.next();
			out += rm.toString();
		}
		out += "---------------------------------------------\n";
		
		return out;
		
	}

	public ArrayList<Venta> getVentas() {
		return ventas;
	}

	public void setVentas(ArrayList<Venta> ventas) {
		this.ventas = ventas;
	}

	public Hashtable<String, Venta> gettVentas() {
		return tVentas;
	}

	public void settVentas(Hashtable<String, Venta> tVentas) {
		this.tVentas = tVentas;
	}

	public ArrayList<Venta> getCobranzas() {
		return cobranzas;
	}

	public void setCobranzas(ArrayList<Venta> cobranzas) {
		this.cobranzas = cobranzas;
	}

	public Hashtable<String, Venta> gettCobranzas() {
		return tCobranzas;
	}

	public void settCobranzas(Hashtable<String, Venta> tCobranzas) {
		this.tCobranzas = tCobranzas;
	}

	public Hashtable<String, ResumenProveedor> getResumenProveedor() {
		return resumenProveedor;
	}

	public void setResumenProveedor(Hashtable<String, ResumenProveedor> resumenProveedor) {
		this.resumenProveedor = resumenProveedor;
	}
	
	
	public Iterator<Venta> getVentasIterator() {
		Collections.sort(this.ventas, new Venta());
		return this.ventas.iterator();
	}

	public Iterator<Venta> getCobranzasIterator() {
		Collections.sort(this.cobranzas, new Venta());
		return this.cobranzas.iterator();
	}

	
	
	public Venta getVenta(String id){
		return this.tVentas.get(id);
	}

	public void addVenta(Venta v, boolean esCobranza){
		if (esCobranza == true){
			this.cobranzas.add(v);
			this.tCobranzas.put(v.getId(), v);
		}else{
			this.ventas.add(v);
			this.tVentas.put(v.getId(), v);
			// Para las marcas de los Crespi
			this.addVentaMarcaVenta(v);
		}

	}

	public double getTotalVenta() {
		return totalVenta;
	}

	public void setTotalVenta(double totalVenta) {
		this.totalVenta = totalVenta;
	}

	public double getTotalContado() {
		return totalContado;
	}

	public void setTotalContado(double totalContado) {
		this.totalContado = totalContado;
	}

	public double getTotalCredito() {
		return totalCredito;
	}

	public void setTotalCredito(double totalCredito) {
		this.totalCredito = totalCredito;
	}
	
	
	
	public String getId() {
		return this.id.trim();
		/*
		String s = "000"+this.id.trim();
		s = s.substring(s.length()-3);
		return s;
		*/
	}

	public String getIdFormato() {
		String s = "000"+this.id.trim();
		s = s.substring(s.length()-3);
		return s;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getUsuario() {
		return usuario;
	}


	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public void calculaTotales(){
		this.totalContado = 0;
		this.totalCredito = 0;
		this.totalVenta = 0;
		int mes = 0;
		for (int i = 0; i < this.ventas.size(); i++) {
			Venta v = this.ventas.get(i);
			if (v.isEsVentaDelMes() == true) {
				mes++;
				this.totalVenta = this.totalVenta + v.getImporte();
				if (v.isContado() == true){
					this.totalContado += v.getImporte();
				}else{
					this.totalCredito += v.getImporte();
				}

			}else{
				Cons.error("venta no mes:" + v.toString());
			}
		}
		
		//System.out.println( mes +"/" +this.ventas.size()+")" + this.getId() +"-" + this.getNombre() +"-"+ this.getUsuario() +"-"+  "Total:"+this.totalVenta);
		
		
	}

	public double getTotalComision() {
		double out = 0;
		Iterator<Venta> ite = this.cobranzas.iterator();
		while (ite.hasNext()) {
			Venta v = ite.next();
			out += v.getComision();
		}
		// sumar ventas asignadas
		Iterator<Venta> iteA = this.ventasMarcaAsignada.iterator();
		while (iteA.hasNext()) {
			Venta v = iteA.next();
			out += (v.getComision() / 2);
		}
		// restar ventas NO asigandas
		Iterator<Venta> iteNoA = this.ventasMarcaNOAsignada.iterator();
		while (iteNoA.hasNext()) {
			Venta v = iteNoA.next();
			out += ((v.getComision() * -1) / 2);
		}
		
		return out;
	}

	
	public double getTotalCobranza() {
		double out = 0;
		Iterator<Venta> ite = this.cobranzas.iterator();
		while (ite.hasNext()) {
			Venta v = ite.next();
			if ((v.getNroRecibo().trim().length() > 2)||(v.getIdTipoMovimiento() == 13)){
				out += v.getImporte();
			}
		}
		return out;
	}

	public double getTotalDevolucion() {
		double out = 0;
		Iterator<Venta> ite = this.ventas.iterator();
		while (ite.hasNext()) {
			Venta v = ite.next();
			if (v.getIdTipoMovimiento() == 13){
				out += v.getImporte();
			}
		}
		return out;
	}

	
	public String printCabeceraArchivo() {
		NumberFormat formatter = new DecimalFormat("###,###,##0.00");
		String strVenta = formatter.format(this.totalVenta);
		String strComision = formatter.format(this.getTotalComision());

		String out = "";
		out += this.getClass().getSimpleName()+" : ("+this.getId()+") ["+this.usuario+"] "+this.nombre + "\n";
		out += "Total de Ventas: " + strVenta + "      Total Comision:" + strComision;		
		return out;
	}

	public void addMarcaAsignada(Venta venta){
		this.ventasMarcaAsignada.add(venta);
	}
	
	public void addMarcaNOAsignada(Venta venta){
		this.ventasMarcaNOAsignada.add(venta);
	}
	
	
	
	
	public ArrayList<Venta> getVentasMarcaAsignada() {
		return ventasMarcaAsignada;
	}


	public ArrayList<Venta> getVentasMarcaNOAsignada() {
		return ventasMarcaNOAsignada;
	}

	

	public Hashtable<String, ResumenMarca> getResumenMarca() {
		return resumenMarca;
	}

	public void setResumenMarca(Hashtable<String, ResumenMarca> resumenMarca) {
		this.resumenMarca = resumenMarca;
	}

	abstract public String printResumen();
}
