import { Request, Response } from 'express';
import { GatewayService } from '../services/GatewayService';

export class GatewayController {
    private gatewayService: GatewayService;

    constructor() {
        this.gatewayService = new GatewayService();
    }

    async heartbeat(req: Request, res: Response): Promise<void> {
        try {
            // 1. Extraemos los datos que nos envía el Gateway (automáticamente) en formato JSON
            const { id_dispositivo, bateria_nivel, ubicacion_lat, ubicacion_lng } = req.body;

            // Validación HTTP rápida: ¿Nos enviaron el ID?
            if (!id_dispositivo) {
                // Si no hay ID, respondemos con un error 400 (Bad Request)
                res.status(400).json({ error: 'El id_dispositivo es obligatorio para el heartbeat' });
                return;
            }

            // 2. Le pasamos los datos al Service para que aplique las reglas de negocio
            const gatewayResult = await this.gatewayService.processHeartbeat(
                id_dispositivo, 
                bateria_nivel, 
                ubicacion_lat, 
                ubicacion_lng
            );

            // 3. Si todo salió bien, respondemos con un código 200 (OK)
            res.status(200).json({
                message: 'Heartbeat procesado correctamente',
                gateway: gatewayResult
            });

        } catch (error: any) {
            // Si el servicio detectó un problema (ej. batería en 150%), devolvemos el error al dispositivo
            res.status(400).json({ error: error.message });
        }
    }
}
