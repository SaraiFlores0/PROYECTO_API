package com.example.practica_api.data

class EstudiantesRepository (
    private val api: ApiService = RetrofitClient.api
) {
    suspend fun list(): List<EstudianteResponse> =
        api.getEstudiantes()

    suspend fun add(nombre: String, edad: Int): Int {
        val res = api.addEstudiante(EstudiantePayload(nombre, edad))
        return res.data ?: -1
    }

    suspend fun update(id: Int, nombre: String, edad: Int): EstudianteResponse? {
        val res = api.updateEstudiante(id, EstudiantePayload(nombre, edad))
        return res.data
    }

    suspend fun delete(id: Int) {
        api.deleteEstudiante(id)
    }
}