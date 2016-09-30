(* Read file line by line until EOF *)
(* Get first and third characters from each line -> they are the keys *)
(* Get second, fourth and fifth characters -> the data in the map *)
(* Put keys and data in the Map *)

let main () = 
    let out_path = Input.get_out_path () in
    let input_syms = Input.get_input () in
        print_endline (out_path ^ ":" ^ input_syms);
    exit (0)
;;


main ();;
