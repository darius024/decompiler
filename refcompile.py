import os
import requests as rq

IN = "src/test/wacc/examples/valid"
ENDPOINT = "https://wacc-vm.doc.ic.ac.uk/compile/plain"

sess = rq.session()

def compile(code):
    body = {
      "flags": {
        "Compile": {
          "opt": {
            "peepholeSimple": False,
            "peepholeMul": False,
            "peepholeSubst": False,
            "betterDivMod": False
          },
          "target": {
            "Amd64": {
              "intel": True
            }
          },
          "outMode": {
            "Asm": {}
          },
          "printAssembly": False,
          "verbose": True,
          "ansi": False
        }
      },
      "file": {
        "UserProvided": {
          "filename": None,
          "body": code
        }
      }
    }
    
    rsp = sess.post(ENDPOINT, json=body)
    return rsp.json()["asm"]


def map_dest(orig):
    return orig.replace("examples", "examples-refcompiled")


try:
    os.removedirs("src/test/wacc/refcompiled")
except:
    pass


for root, _, files in os.walk(IN):
    files = [f for f in files if f[-5:] == ".wacc"]
    try:
        os.makedirs(map_dest(root))
    except:
        pass
    
    for p in files:
        with open(f"{root}/{p}", "r") as f:
            code = f.read()
            
        asm = compile(code)
        
        with open(f"{map_dest(root)}/{p.replace(".wacc", "-ref.s")}", "w") as f:
            f.write(asm)