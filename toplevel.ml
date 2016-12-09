
let prompt = ">> " (* Prompt displayed to the user *)
let welcome_msg = 
"Welcome, this is a lambda-calculus toplevel interpreter based on Benjamin C.\n\
Pierce implementations available at https://www.cis.upenn.edu/~bcpierce/tapl/\n"

(* lazo is the top level interface *)
let main () =
print_endline welcome_msg;
try 
    while true do
        print_string prompt;

            let linea = read_line () in
            if String.length linea = 0 then print_string "" (* Nothing got read *)
            else begin
                print_endline ("Read: " ^ linea)
            end
    done
with End_of_file -> print_endline "Goodbye!";;

main () (* Call the main function with unit *)
