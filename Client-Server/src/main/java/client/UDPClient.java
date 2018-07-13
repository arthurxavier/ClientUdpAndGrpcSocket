package client;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient{
	public static void main(String args[]) throws Exception{
	
 		//Pegando o ip do servidor
 		InetAddress IPAddress = InetAddress.getByName("localhost");
 		
		String data = null;
 		//Classe para ler do teclado
 		Scanner s = new Scanner(System.in);
 		
		while(true) {
 			System.out.println("Bem vindo cliente UDP! \n CREATE | READ | UPDATE | DELETE | OBSERVE");
 			String comando = s.nextLine();
 			if(comando.equals("READ") || comando.equals("DELETE") || comando.equals("OBSERVE")){
 				System.out.println("Digite a chave:");
 				BigInteger chave = new BigInteger(s.nextLine());
 				new ThreadUDPClient(comando,chave,"").start();;
 			}
			if(comando.equals("UPDATE") || comando.equals("CREATE")){
 				System.out.println("Digite a chave a ser colocada:");
 				BigInteger chave = new BigInteger(s.nextLine());
 				System.out.println("Digite a mensagem a ser enviada:");
 				String mensagem = s.nextLine();
 				new ThreadUDPClient(comando,chave,mensagem).start();;
 			} else {
			System.out.println("Comando Inválido!");
		}
		
 		}
	}
 }
