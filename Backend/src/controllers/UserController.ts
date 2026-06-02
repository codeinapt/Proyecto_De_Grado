import { Request, Response } from 'express';
import { UserService } from '../services/UserService';

export class UserController {
    private userService: UserService;

    constructor() {
        this.userService = new UserService();
    }

    // Nota que aquí usamos req (Request) y res (Response) porque hablamos de HTTP
    async register(req: Request, res: Response): Promise<void> {
        try {
            // 1. Extraemos los datos que nos manda el celular en el JSON
            const { nombre_usuario, public_key } = req.body;

            // Validación rápida: ¿Nos enviaron todo?
            if (!nombre_usuario || !public_key) {
                // Respondemos 400 Bad Request
                res.status(400).json({ error: 'nombre_usuario y public_key son obligatorios' });
                return; // Cortamos la ejecución aquí
            }

            // 2. Le pasamos los datos al Service para que aplique las reglas de negocio
            const newUser = await this.userService.registerUser(nombre_usuario, public_key);

            // 3. Si todo salió bien, respondemos con código 201 (Created)
            res.status(201).json({
                message: 'Usuario registrado exitosamente',
                user: newUser
            });

        } catch (error: any) {
            // 4. Si el Service lanzó un "throw new Error()", lo atrapamos aquí
            // y le devolvemos un 400 al celular con el mensaje del error
            res.status(400).json({ error: error.message });
        }
    }
}
