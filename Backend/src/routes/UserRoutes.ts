import { Router } from 'express';
import { UserController } from '../controllers/UserController';

// Creamos un "Router", que es como un mini-agrupador de rutas
const router = Router();

// Instanciamos nuestro controlador
const userController = new UserController();

// Le decimos que cuando llegue una petición POST a "/register", ejecute la función del controlador.
// (El .bind() es necesario en JavaScript cuando pasas una función de una clase como parámetro)
router.post('/register', userController.register.bind(userController));

// Exportamos el router para conectarlo a la app principal
export default router;
