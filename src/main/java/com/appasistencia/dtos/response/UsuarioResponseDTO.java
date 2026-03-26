package com.appasistencia.dtos.response;

import com.appasistencia.models.Genero;
import com.appasistencia.models.Rol;
import com.appasistencia.models.TipoDocumento;
import com.appasistencia.models.Usuario;
import java.time.LocalDateTime;

// DTO de respuesta: datos completos de un usuario (sin contrasena)
public class UsuarioResponseDTO {

    // Datos personales
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
    // Documentacion e identidad
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private Genero genero;
    // Rol y perfil
    private Rol rol;
    private String fotoPerfil;
    // Relacion (ID plano, no objeto anidado)
    private Long idInstitucion;
    // Campos de auditoria
    private LocalDateTime fechaCreacion;
    private boolean activo;
    private boolean verificado;

    public UsuarioResponseDTO() {}

    // Conversion desde entidad - extrae idInstitucion como ID plano
    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.idUsuario = usuario.getIdUsuario();
        dto.nombre = usuario.getNombre();
        dto.apellido = usuario.getApellido();
        dto.email = usuario.getEmail();
        dto.telefono = usuario.getTelefono();
        dto.direccion = usuario.getDireccion();
        dto.tipoDocumento = usuario.getTipoDocumento();
        dto.numeroDocumento = usuario.getNumeroDocumento();
        dto.genero = usuario.getGenero();
        dto.rol = usuario.getRol();
        dto.fotoPerfil = usuario.getFotoPerfil();
        if (usuario.getInstitucion() != null) {
            dto.idInstitucion = usuario.getInstitucion().getIdInstitucion();
        }
        dto.fechaCreacion = usuario.getFechaCreacion();
        dto.activo = usuario.isActivo();
        dto.verificado = usuario.isVerificado();
        return dto;
    }

    // Getters
    public Long getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public Genero getGenero() { return genero; }
    public Rol getRol() { return rol; }
    public String getFotoPerfil() { return fotoPerfil; }
    public Long getIdInstitucion() { return idInstitucion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public boolean isActivo() { return activo; }
    public boolean isVerificado() { return verificado; }
}
