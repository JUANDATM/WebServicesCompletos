package com.example.mywebservices

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.example.sqlite.admDB
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.lang.reflect.Executable
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {
    var wsConsultar : String = "http://192.168.7.112/Servicios/MostrarAlumno.php"//AGREGAMOS LA DIRECCIONDE DONDE SE VA A CONSUMIR EL SERVICIO WEB
    var wsInsertar : String = "http://192.168.7.112/Servicios/insertarAlumno.php"//
    var wsEliminar : String = "http://192.168.7.112/Servicios/BorrarAlumno.php"//
    var wsActualizar : String = "http://192.168.7.112/Servicios/ActualizarAlumno.php"//
    var wsMostrar : String = "http://192.168.7.112/Servicios/MostrarAlumnos.php"//
    var hilo : ObtenerUnServicioWeb? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun consultaXNoControl (v : View) { //CREAMOS UNA CLASE DONDE VERIFICAREMOS QUE EL CAMPO NECESARIO PARA BUSCAR EL ALUMNO  NO ESTE VACIA
        if(etNoControl.text.isEmpty()){
            Toast.makeText(this, "FALTA INGRESAR NUMERO DE CONTROL", Toast.LENGTH_SHORT).show();
            etNoControl.requestFocus()
        }else {
            val no = etNoControl.text.toString()
            hilo = ObtenerUnServicioWeb()
            hilo?.execute("Consulta", no,"","","")
        }
    }
    fun consulta (v : View) {

            hilo = ObtenerUnServicioWeb()
            hilo?.execute("Mostrar","","","","")

    }
    fun eliminarAlumno (v : View) {//VERIFICAMOS QUE EL CAMPO QUE NECESITAMOS PARA PODER ELIMINAR AL ALUMNO NO ESTE VACIO
        if(etNoControl.text.isEmpty()){
            Toast.makeText(this, "FALTA INGRESAR NUMERO DE CONTROL", Toast.LENGTH_SHORT).show();
            etNoControl.requestFocus()
        }else {
            val no = etNoControl.text.toString()
            hilo = ObtenerUnServicioWeb()
            hilo?.execute("Eliminar", no,"","","")
        }
    }
    fun insertAlumno(v : View){ // SE CREA LA FUNCION , SE VERIFICAN QUE LOS CAMPOS NO ESTEN VACIOS , SE EXTRAEN LOS DATOS Y SE ENVIAN

        if (etNoControl.text.isEmpty() || etNombre.text.isEmpty() || etCarrera.text.isEmpty() || etTelefono.text.isEmpty() ){
            Toast.makeText(this, "ERROR:  FALTAN TODOS LOS DATOS POR LLENAR", Toast.LENGTH_SHORT).show();
        }else {
            val no = etNoControl.text.toString()
            val nom = etNombre.text.toString()
            val carr = etCarrera.text.toString()
            val tel = etTelefono.text.toString()
            hilo = ObtenerUnServicioWeb()
            hilo?.execute("Insertar", no,carr,nom,tel)
        }
    }
    fun actualizatAlumno(v : View){ // SE CREA LA FUNCION , SE VERIFICAN QUE LOS CAMPOS NO ESTEN VACIOS , SE EXTRAEN LOS DATOS Y SE ENVIAN

        if (etNoControl.text.isEmpty() || etNombre.text.isEmpty() || etCarrera.text.isEmpty() || etTelefono.text.isEmpty() ){
            Toast.makeText(this, "ERROR:  FALTAN TODOS LOS DATOS POR LLENAR", Toast.LENGTH_SHORT).show();
        }else {
            val no = etNoControl.text.toString()
            val nom = etNombre.text.toString()
            val carr = etCarrera.text.toString()
            val tel = etTelefono.text.toString()
            hilo = ObtenerUnServicioWeb()
            hilo?.execute("Actualizar", no,carr,nom,tel)
        }
    }

    inner class ObtenerUnServicioWeb():AsyncTask<String, String, String>(){

        override fun doInBackground(vararg params: String?): String {
            var Url : URL? = null
            var sResultado = ""
            try {
                val urlConn:HttpURLConnection
                val printout:DataOutputStream
                var input : DataOutputStream
                if (params[0].toString() == "Consulta") {
                    Url = URL(wsConsultar)
                }else if(params[0].toString() == "Eliminar"){
                    Url = URL(wsEliminar)
                }else if (params[0].toString() == "Insertar"){
                    Url= URL(wsInsertar)
                }else if(params[0].toString() == "Actualizar"){
                    Url= URL(wsActualizar)
                }else if (params[0].toString() == "Mostrar"){
                    Url= URL(wsMostrar)
                }
                urlConn = Url?.openConnection() as HttpURLConnection
                urlConn.doInput = true
                urlConn.doOutput = true
                urlConn.useCaches=false
                urlConn.setRequestProperty("Content-Type","Aplication/json")//TIPO DE DATO QUE RESIBIRA
                urlConn.setRequestProperty("Accept","aplication/json")
                urlConn.connect()
                //PREPARAR LOS DATOS A ENVIAR AL WEB SERVICE
                val jsonParam = JSONObject()
                jsonParam.put("nocontrol",params[1])
                jsonParam.put("carrera",params[2])
                jsonParam.put("nombre",params[3])
                jsonParam.put("telefono",params[4])
                val os = urlConn.outputStream
                val writer = BufferedWriter(OutputStreamWriter(os,"UTF-8"))
                writer.write(jsonParam.toString())
                writer.flush()
                writer.close()
                val respuesta= urlConn.responseCode
                val result = StringBuilder()
                if (respuesta == HttpURLConnection.HTTP_OK){
                    val inStream : InputStream = urlConn.inputStream
                    val isReader = InputStreamReader(inStream)
                    val bReader = BufferedReader(isReader)
                    var tempStr : String?
                    while (true){
                        tempStr = bReader.readLine()
                        if (tempStr == null){
                            break
                        }
                        result.append(tempStr)
                    }
                    urlConn.disconnect()
                    sResultado = result.toString()
                }
            }catch (e: MalformedURLException){
                Log.d("JDTM",e.message)
            }catch (e: IOException){
                Log.d("JDTM",e.message)
            }catch (e: JSONException){
                Log.d("JDTM",e.message)
            }catch (e: Exception){
                Log.d("JDTM",e.message)
            }
           return sResultado
        }//Fin doInBackground    //Metodos que se van a utilizar
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            var no :String= ""
            var nom :String= ""
            var carr :String= ""
            var tel :String= ""
            try {
                val respuestaJSON = JSONObject(result)
                val resultJSON = respuestaJSON.getString("success")
                val msgJSON = respuestaJSON.getString("message")

                when {
                    resultJSON == "200" ->{
                        val alumnoJSON = respuestaJSON.getJSONArray("alumno")
                        if (alumnoJSON.length() >= 1) {
                            no = alumnoJSON.getJSONObject(0).getString("nocontrol")
                            nom = alumnoJSON.getJSONObject(0).getString("nombre")
                            carr = alumnoJSON.getJSONObject(0).getString("carrera")
                            tel = alumnoJSON.getJSONObject(0).getString("telefono")
                            etNoControl.setText(no)
                            etNombre.setText(nom)
                            etCarrera.setText(carr)
                            etTelefono.setText(tel)
                        }
                    }
                    resultJSON == "202" ->{
                        val alumnoJSON = respuestaJSON.getJSONArray("alumno")
                        if (alumnoJSON.length() >= 1) {
                            var admin = admDB(baseContext)
                            for (i in 0 until alumnoJSON.length()) {
                                no = alumnoJSON.getJSONObject(i).getString("nocontrol")
                                nom = alumnoJSON.getJSONObject(i).getString("nombre")
                                carr = alumnoJSON.getJSONObject(i).getString("carrera")
                                tel = alumnoJSON.getJSONObject(i).getString("telefono")
                                val sentencia = "INSERT INTO alumno(nocontrol,nombre,carrera,telefono) VALUES ('$no','$nom','$carr','$tel')"
                                admin.Ejecuta(sentencia)
                            }
                            Toast.makeText(baseContext, "Alumnos Almacenados Local", Toast.LENGTH_SHORT).show();

                        }
                    }
                    resultJSON == "201" ->{// SI LA OPERACION DEL JSON ES EXITOSA SE ENVIA EL SIGUIENTE MENSAJE
                        Toast.makeText(baseContext, msgJSON, Toast.LENGTH_SHORT).show();
                        etTelefono.setText("")
                        etCarrera.setText("")
                        etNombre.setText("")
                        etNoControl.setText("")
                        etNoControl.requestFocus()
                    }
                    resultJSON == "204" ->{// SI LA OPERACION ES ERRONEA SE MANDA EL SIGUIENTE MENSAJE
                        Toast.makeText(baseContext, "ERROR : ALUMNO NO ENCONTRADO", Toast.LENGTH_SHORT).show();
                    }
                    resultJSON == "409" ->{// SI POR ALGUNA RAZON NO EXISTE LA OPERACON , SE ENVIA LA SIGUIENTE
                        Toast.makeText(baseContext, "ERROR : AL AGREGAR ALUMNO", Toast.LENGTH_SHORT).show();
                    }
                }
            }catch (e: JSONException) {
                Log.d("JDTM", e.message)
            }catch (e: Exception) {
                Log.d("JDTM", e.message)
            }

        }//fin  onPostExecute //Metodos que se van a utilizar

    }// Fin  ObtenerUnServicioWeb

}//Fin De La clase MainActivity
