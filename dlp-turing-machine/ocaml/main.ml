(* Debug function used to print machine's description list *)
let rec print_list l = match l with
    | [] -> ()
    | [hd] -> print_endline hd
    | hd :: tl -> print_endline hd; print_list tl

let main () = 
    let out_path = Io.get_out_path () in 
    let input_syms = Io.get_input () in
    let m_desc = List.rev (Io.read_machine_desc ()) in
        (* print_endline (out_path ^ " : " ^ input_syms); *) (* debug *)
        (* print_list m_desc; *) (* debug *) 
        (* Engine.print_m_desc m_desc; *)(* debug *)
        (* Engine.test_engine; *) (* debug *)
    let accept,steps,tape = Engine.run_machine input_syms m_desc in
        Io.print_output accept steps;
        if not ((Pervasives.compare out_path "") = 0) then
            Io.string_to_file tape out_path;
    exit (0);;

main ()
