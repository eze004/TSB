package clases;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Una clase con un main() simple para probar la clase TSB_OAHashtable.
 * @author Ing. Valerio Frittelli.
 * @version Octubre de 2017.
 */
public class Test 
{
    public static void main(String args[])
    {
        // una tabla "corta" con factor de carga pequeño...
        TSB_OAHashtable<Integer, String> ht1 = new TSB_OAHashtable<>(3, 0.8f);
        System.out.println("Contenido inicial: " + ht1);
        
        // algunas inserciones...
        ht1.put(Math.round((float) Math.random()*10000), "Argentina");
        ht1.put(Math.round((float) Math.random()*10000), "Brasil");
        ht1.put(Math.round((float) Math.random()*10000), "Chile");
        System.out.println("Luego de 3 inserciones: " + ht1);

        ht1.put(Math.round((float) Math.random()*10000), "Mexico");
        ht1.put(Math.round((float) Math.random()*10000), "Uruguay");
        ht1.put(Math.round((float) Math.random()*10000), "Perú");
        ht1.put(Math.round((float) Math.random()*10000), "Colombia");
        ht1.put(Math.round((float) Math.random()*10000), "Ecuador");
        ht1.put(Math.round((float) Math.random()*10000), "Paraguay");
        ht1.put(Math.round((float) Math.random()*10000), "Bolivia");
        ht1.put(Math.round((float) Math.random()*10000), "Venezuela");
        ht1.put(Math.round((float) Math.random()*10000), "Estados Unidos");
        System.out.println("Luego de algunas inserciones: " + ht1);
        
        TSB_OAHashtable<Integer, String> ht2 = new TSB_OAHashtable<>(ht1);
        System.out.println("Segunda tabla: " + ht2);
        
        System.out.println("Tabla 1 recorrida a partir de una vista: ");
        Set<Map.Entry<Integer, String>> se = ht1.entrySet();
        Iterator<Map.Entry<Integer, String>> it = se.iterator();
        while(it.hasNext())
        {
            Map.Entry<Integer, String> entry = it.next();
            System.out.println("Par: " + entry);
        }
    }
}
