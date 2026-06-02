import { GatewayRepository } from '../repositories/GatewayRepository';
import { Gateway } from '../models/GatewayModel';

export class GatewayService {
    private gatewayRepository: GatewayRepository;

    constructor() {
        this.gatewayRepository = new GatewayRepository();
    }

    // Método principal del negocio para procesar el Heartbeat
    async processHeartbeat(id_dispositivo: string, bateria_nivel?: number, lat?: number, lng?: number): Promise<Gateway> {
        
        // REGLA DE NEGOCIO 1:
        // Si el gateway envía un nivel de batería, validamos que tenga un valor lógico (entre 0 y 100).
        if (bateria_nivel !== undefined) {
            if (bateria_nivel < 0 || bateria_nivel > 100) {
                throw new Error('El nivel de batería debe ser un valor entre 0 y 100');
            }
        }

        // REGLA DE NEGOCIO 2:
        // Siempre le ponemos la fecha y hora exacta del servidor en el momento en que recibimos el mensaje.
        const gatewayData: Gateway = {
            id_dispositivo: id_dispositivo,
            bateria_nivel: bateria_nivel,
            ultima_sincronizacion: new Date(), // <-- Aquí capturamos la hora actual
            ubicacion_lat: lat,
            ubicacion_lng: lng
        };

        // Pasamos el objeto al repositorio para que haga su "magia" (el Upsert) en la BD
        const savedGateway = await this.gatewayRepository.upsertGateway(gatewayData);

        return savedGateway;
    }
}
