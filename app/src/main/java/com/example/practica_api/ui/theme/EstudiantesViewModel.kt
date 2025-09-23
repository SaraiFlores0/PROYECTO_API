package com.example.practica_api.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica_api.data.EstudianteResponse
import com.example.practica_api.data.EstudiantesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UiState(
    val lista: List<EstudianteResponse> = emptyList(),
    val nombre: String = "",
    val edad: String = "",
    val seleccionadoId: Int? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val info: String? = null
)

class EstudiantesViewModel(
    private val repo: EstudiantesRepository = EstudiantesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init { refresh() }

    fun setNombre(value: String) { _uiState.value = _uiState.value.copy(nombre = value) }
    fun setEdad(value: String)   { _uiState.value = _uiState.value.copy(edad = value) }

    fun seleccionar(est: EstudianteResponse) {
        _uiState.value = _uiState.value.copy(
            seleccionadoId = est.id,
            nombre = est.nombre,
            edad = est.edad.toString(),
            info = "Seleccionado ID ${est.id}"
        )
    }

    fun limpiarSeleccion() {
        _uiState.value = _uiState.value.copy(
            seleccionadoId = null, nombre = "", edad = "", info = "Limpio"
        )
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null, info = null)
            try {
                val data = repo.list()
                _uiState.value = _uiState.value.copy(lista = data, loading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun agregar() {
        val nombre = _uiState.value.nombre.trim()
        val edad = _uiState.value.edad.toIntOrNull()
        if (nombre.isEmpty() || edad == null) {
            _uiState.value = _uiState.value.copy(error = "Nombre y edad válidos requeridos")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null, info = null)
            try {
                repo.add(nombre, edad)
                refresh()
                _uiState.value = _uiState.value.copy(info = "Insertado", nombre = "", edad = "")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun actualizar() {
        val id = _uiState.value.seleccionadoId ?: run {
            _uiState.value = _uiState.value.copy(error = "Selecciona un estudiante")
            return
        }
        val nombre = _uiState.value.nombre.trim()
        val edad = _uiState.value.edad.toIntOrNull()
        if (nombre.isEmpty() || edad == null) {
            _uiState.value = _uiState.value.copy(error = "Nombre y edad válidos requeridos")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null, info = null)
            try {
                repo.update(id, nombre, edad)
                refresh()
                _uiState.value = _uiState.value.copy(info = "Actualizado")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun eliminar(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null, info = null)
            try {
                repo.delete(id)
                refresh()
                _uiState.value = _uiState.value.copy(info = "Eliminado")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(loading = false, error = e.message)
            }
        }
    }
}
