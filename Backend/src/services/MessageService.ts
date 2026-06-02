import { MessageRepository } from '../repositories/MessageRepository';
import { Message, EstadoMensaje } from '../models/MessageModel';

export class MessageService {
    private messageRepository: MessageRepository;

    constructor() {
        this.messageRepository = new MessageRepository();
    }

    // Método para procesar el lote (batch) de mensajes entrantes
    async processIncomingMessages(gateway_id: string, messages: any[]): Promise<number> {
        
        // REGLA DE NEGOCIO 1: Validar que el arreglo de mensajes exista
        if (!messages || !Array.isArray(messages)) {
            throw new Error('El formato de los mensajes no es un arreglo válido');
        }

        // Preparamos los mensajes limpios para enviarlos al Repositorio
        const cleanMessages: Message[] = [];
        const serverTime = new Date(); // La hora oficial en la que el backend procesa el lote

        for (const msg of messages) {
            // REGLA DE NEGOCIO 2: Estructurar la información y estampar la hora del servidor
            // Ignoramos cualquier estado o fecha_servidor que intente engañarnos enviando el móvil
            const cleanMsg: Message = {
                uuid_mensaje: msg.uuid_mensaje,
                emisor_id: msg.emisor_id,
                receptor_id: msg.receptor_id,
                contenido_cifrado: msg.contenido_cifrado,
                tipo_mensaje: msg.tipo_mensaje || 'TEXTO',
                estado: EstadoMensaje.RECIBIDO_EN_NUBE, // Lo forzamos a RECIBIDO
                contador_saltos: Number.isInteger(msg.contador_saltos) ? msg.contador_saltos : 0,
                fecha_emision_movil: new Date(msg.fecha_emision_movil),
                fecha_servidor: serverTime // Estampilla oficial del servidor
            };

            // Solo agregamos mensajes que tengan la información vital (ignoramos los mal formados)
            if (cleanMsg.uuid_mensaje && cleanMsg.emisor_id && cleanMsg.receptor_id && cleanMsg.contenido_cifrado) {
                cleanMessages.push(cleanMsg);
            }
        }

        // Finalmente, pasamos el arreglo limpio al repositorio para su inserción
        const insertedCount = await this.messageRepository.saveBatch(cleanMessages);

        return insertedCount;
    }

    async getPendingMessages(gateway_id: string): Promise<Message[]> {
        // Por ahora retornamos los mensajes pendientes en estado RECIBIDO_EN_NUBE.
        // En futuras mejoras, podríamos filtrar por reglas de ruteo específicas del gateway.
        return this.messageRepository.getAndMarkPendingMessages();
    }

    async acknowledgeMessage(uuid_mensaje: string, receptor_id: string): Promise<Message> {
        if (!uuid_mensaje || !receptor_id) {
            throw new Error('uuid_mensaje y receptor_id son obligatorios para el ACK');
        }

        return this.messageRepository.acknowledgeMessage(uuid_mensaje, receptor_id);
    }
}
