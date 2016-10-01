(* Read file line by line until EOF *)
(* Get first and third characters from each line -> they are the keys *)
(* Get second, fourth and fifth characters -> the data in the map *)
(* Put keys and data in the Map *)

(* Debug function used to print machine's description list *)
let rec print_list l = match l with
    | [] -> ()
    | [hd] -> print_endline hd
    | hd :: tl -> print_endline hd; print_list tl

let main () = 
    let out_path = Input.get_out_path () in
    let input_syms = Input.get_input () in
    let m_desc = Input.read_machine_desc () in
        (* print_endline (out_path ^ " : " ^ input_syms); *) (* debug *)
        (* print_list m_desc; *) (* debug *)
        (* Engine.print_m_desc m_desc; *) (* debug *)
        exit (0);;

main ()
