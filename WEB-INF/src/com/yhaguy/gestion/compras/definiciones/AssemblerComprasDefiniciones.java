package com.yhaguy.gestion.compras.definiciones;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TipoCambio;

public class AssemblerComprasDefiniciones extends Assembler{


	
	Hashtable<String, MyArray> hashTipoCambio ;
	
	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	public List<MyArray> refreshListaTipoDeCambioAss(long idMoneda) throws Exception{
		
		this.hashTipoCambio = new Hashtable<String, MyArray>();
		List<MyArray> listaTipoDeCambio = new ArrayList<MyArray>();
		Enumeration<MyArray> enumeration;
		RegisterDomain rr = RegisterDomain.getInstance();
		List<TipoCambio> list = rr.getListaTipoDeCambioByMoneda(idMoneda);
		for (TipoCambio tipoCambio : list) {						
			MyArray tipoCambioToHash = new MyArray(); 
			String hashKeyFecha = "";			
			hashKeyFecha=tipoCambio.getFecha().toString().substring(0, 10);
			if(this.hashTipoCambio.containsKey(hashKeyFecha)){
				tipoCambioToHash = this.hashTipoCambio.get(hashKeyFecha);
				if(tipoCambioToHash.getPos1()== ""){
					tipoCambioToHash.setPos1(tipoCambio.getCompra());
					tipoCambioToHash.setPos2(tipoCambio.getVenta());
				}else if(tipoCambioToHash.getPos3()== ""){
					tipoCambioToHash.setPos3(tipoCambio.getCompra());
					tipoCambioToHash.setPos4(tipoCambio.getVenta());
				}
			}else{
				tipoCambioToHash.setPos5(hashKeyFecha);
				if(tipoCambio.getTipoCambio().getSigla().equals(Configuracion.SIGLA_TIPO_CAMBIO_BCP)){
					tipoCambioToHash.setPos1(tipoCambio.getCompra());
					tipoCambioToHash.setPos2(tipoCambio.getVenta());
				}else if(tipoCambio.getTipoCambio().getSigla().equals(Configuracion.SIGLA_TIPO_CAMBIO_APP)){
					tipoCambioToHash.setPos3(tipoCambio.getCompra());
					tipoCambioToHash.setPos4(tipoCambio.getVenta());
				}
				this.hashTipoCambio.put(hashKeyFecha, tipoCambioToHash);
			}			
		}
		enumeration = this.hashTipoCambio.elements();
		while(enumeration.hasMoreElements()){
			listaTipoDeCambio.add(enumeration.nextElement());
		}	
		
		return listaTipoDeCambio;		
	}
	
	public void saveTipoCambioAss(MyArray tipoCambio) throws Exception{
		
		MyPair moneda = (MyPair) tipoCambio.getPos1();
		MyPair tc = (MyPair) tipoCambio.getPos2();		
		
		RegisterDomain rr = RegisterDomain.getInstance();
		Tipo tmoneda = rr.getTipoPorSigla(moneda.getSigla());
		Tipo ttc = rr.getTipoPorSigla(tc.getSigla());		
		
		TipoCambio tipoCambioDomain = new TipoCambio();

		tipoCambioDomain.setMoneda(tmoneda);
		tipoCambioDomain.setTipoCambio(ttc);
		tipoCambioDomain.setFecha((Date) tipoCambio.getPos3());
		tipoCambioDomain.setCompra((double) tipoCambio.getPos4());
		tipoCambioDomain.setVenta((double) tipoCambio.getPos5());
		tipoCambioDomain.setFechaString(tipoCambio.getPos6().toString());
		
		rr.saveTipoCambio(tipoCambioDomain);
		
		
	}
	 

}
